package org.metadatacenter.spreadsheetvalidator.validator;

import com.github.benmanes.caffeine.cache.Cache;
import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.URL;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.*;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class UrlValidator extends InputValueValidator {

  private static final int CONNECTION_TIMEOUT = 8000; // 8 seconds
  private static final int READ_TIMEOUT = 5000;       // 5 seconds
  private static final int MAX_RETRIES = 10;          // Max retries for 429
  private static final int BASE_BACKOFF = 500;        // Initial backoff time

  private final Cache<String, Boolean> urlStatusCache; // URL cache

  @Inject
  public UrlValidator(@Nonnull Cache<String, Boolean> urlStatusCache) {
    this.urlStatusCache = checkNotNull(urlStatusCache);
  }

  @Override
  public Optional<ValidationError> validateInputValue(@Nonnull Object value,
                                                      @Nonnull ValueContext valueContext,
                                                      @Nonnull ValidatorContext validatorContext) {
    var columnDescription = valueContext.getColumnDescription();
    var valueType = columnDescription.getColumnType();

    if (valueType == URL) {
      try {
        var url = new URL(value.toString());

        // Use the cache to speed up the URL checking
        if (isCachedResolvable(url)) {
          return Optional.empty();
        }
        var resolvable = isResolvable(url);
        urlStatusCache.put(url.toString(), resolvable);

        if (!resolvable) {
          return Optional.of(createValidationError(value, valueContext, "URL is not resolvable"));
        }
      } catch (MalformedURLException e) {
        return Optional.of(createValidationError(value, valueContext, "URL is not valid"));
      }
    }
    return Optional.empty();
  }

  private boolean isCachedResolvable(URL url) {
    var statusMap = urlStatusCache.asMap();
    return statusMap.containsKey(url.toString()) && statusMap.get(url.toString());
  }

  public boolean isResolvable(URL url) {
    // Try resolving with HEAD first
    return checkUrl(url, "HEAD") || checkUrl(url, "GET");
  }

  private boolean checkUrl(URL url, String method) {
    var attempt = 0;
    while (attempt <= MAX_RETRIES) {
      int responseCode = -1;
      try {
        var conn = setupConnection(url, method);
        responseCode = conn.getResponseCode();

        // If redirect (3xx), follow the new location
        if (isRedirect(responseCode)) {
          return handleRedirect(url, conn);
        }
        // Handle 429 Too Many Requests with exponential backoff
        if (isTooManyRequests(responseCode)) {
          applyBackoff(attempt);
          attempt++;
          continue;
        }
        // Return true for successful response (2xx)
        return (responseCode >= 200 && responseCode < 300);
      } catch (IOException | InterruptedException e) {
        return false;
      }
    }
    // If all attempts fail, assume it's possibly resolvable
    System.out.println("WARN  All attempts failed; assuming " + url + " might still be resolvable.");
    return true;
  }

  private HttpURLConnection setupConnection(URL url, String method) throws IOException {
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod(method);
    conn.setConnectTimeout(CONNECTION_TIMEOUT);
    conn.setReadTimeout(READ_TIMEOUT);
    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36");
    conn.connect();
    return conn;
  }

  private boolean isRedirect(int responseCode) {
    return responseCode >= 300 && responseCode < 400;
  }

  private boolean handleRedirect(URL url, HttpURLConnection conn) throws IOException {
    String newLocation = conn.getHeaderField("Location");
    if (newLocation != null) {
      URL redirectUrl = new URL(url, newLocation);
      return isResolvable(redirectUrl);
    }
    return false;
  }

  private boolean isTooManyRequests(int responseCode) {
    return responseCode == 429;
  }

  private void applyBackoff(int attempt) throws InterruptedException {
    int backoff = BASE_BACKOFF * attempt;
    TimeUnit.MILLISECONDS.sleep(backoff);
  }

  private ValidationError createValidationError(Object value, ValueContext valueContext, String errorMessage) {
    var columnDescription = valueContext.getColumnDescription();
    return ValidationError.builder()
        .setErrorType("invalidUrl")
        .setErrorMessage(errorMessage)
        .setErrorLocation(valueContext.getColumn(), valueContext.getRow())
        .setOtherProp(VALUE, value)
        .setOtherProp(COLUMN_LABEL, columnDescription.getColumnLabel())
        .setOtherProp(SEVERITY, 1)
        .build();
  }
}
