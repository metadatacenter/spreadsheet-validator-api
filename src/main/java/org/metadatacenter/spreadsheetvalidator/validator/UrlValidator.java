package org.metadatacenter.spreadsheetvalidator.validator;

import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.URL;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.COLUMN_LABEL;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.VALUE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class UrlValidator extends InputValueValidator {

  private static final int CONNECTION_TIMEOUT = 5000; // 5 seconds
  private static final int READ_TIMEOUT = 5000;       // 5 seconds
  private static final int MAX_RETRIES = 3;           // Max retries for 429
  private static final int BASE_BACKOFF = 1000;       // Initial backoff time

  @Override
  public Optional<ValidationError> validateInputValue(@Nonnull Object value,
                                                      @Nonnull ValueContext valueContext,
                                                      @Nonnull ValidatorContext validatorContext) {
    var columnDescription = valueContext.getColumnDescription();
    var valueType = columnDescription.getColumnType();

    if (valueType == URL) {
      try {
        var url = new URL(value.toString());
        if (!isResolvable(url)) {
          return Optional.of(createValidationError(value, valueContext, "URL is not resolvable"));
        }
      } catch (MalformedURLException e) {
        return Optional.of(createValidationError(value, valueContext, "URL is not valid"));
      }
    }
    return Optional.empty();
  }

  public boolean isResolvable(URL url) {
    // Try resolving with HEAD first
    return checkUrl(url, "HEAD") || checkUrl(url, "GET");
  }

  private boolean checkUrl(URL url, String method) {
    var attempt = 0;
    while (attempt <= MAX_RETRIES) {
      try {
        var conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setConnectTimeout(CONNECTION_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36");
        conn.connect();

        int responseCode = conn.getResponseCode();

        // If redirect (3xx), follow the new location
        if (responseCode >= 300 && responseCode < 400) {
          var newLocation = conn.getHeaderField("Location");
          if (newLocation != null) {
            var redirectUrl = new URL(url, newLocation);
            return isResolvable(redirectUrl);
          }
          return false;
        }
        // Handle 429 Too Many Requests with exponential backoff
        if (responseCode == 429) {
          int backoff = BASE_BACKOFF * attempt;
          TimeUnit.MILLISECONDS.sleep(backoff);
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
