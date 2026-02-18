package org.metadatacenter.spreadsheetvalidator.validator;

import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.URL;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ERROR_TYPE;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.VALUE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class UrlValidator extends InputValueValidator {
  @Override
  public void validateInputValue(@Nonnull Object value,
                                 @Nonnull ValueContext valueContext,
                                 @Nonnull ValidatorContext validatorContext) {
    var valueType = valueContext.getColumnDescription().getColumnType();
    if (valueType == URL) {
      try {
        var url = new URL(String.valueOf(value));
        if (!isResolvable(url)) {
          validatorContext.getValidationResult().add(
              invalidUrlError(valueContext.getColumn(), valueContext.getRow(), value,
                  "URL does not exist")
          );
        }
      } catch (MalformedURLException e) {
        validatorContext.getValidationResult().add(
            invalidUrlError(valueContext.getColumn(), valueContext.getRow(), value,
                "URL is not valid")
        );
      }
    }
  }

  private boolean isResolvable(URL url) {
    try {
      var conn = (HttpURLConnection) url.openConnection();
      conn.setInstanceFollowRedirects(true);
      conn.setRequestMethod("HEAD");
      conn.setConnectTimeout(5000);
      conn.setReadTimeout(5000);
      int responseCode = conn.getResponseCode();
      // Follow redirects manually if needed (e.g., cross-protocol redirects)
      if (responseCode == HttpURLConnection.HTTP_MOVED_PERM
          || responseCode == HttpURLConnection.HTTP_MOVED_TEMP
          || responseCode == 307 || responseCode == 308) {
        var redirectUrl = new URL(conn.getHeaderField("Location"));
        conn.disconnect();
        return isResolvable(redirectUrl);
      }
      conn.disconnect();
      // If HEAD returns 404, retry with GET as some servers don't support HEAD
      if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
        return isResolvableWithGet(url);
      }
      return responseCode != HttpURLConnection.HTTP_NOT_FOUND;
    } catch (IOException e) {
      return false;
    }
  }

  private boolean isResolvableWithGet(URL url) {
    try {
      var conn = (HttpURLConnection) url.openConnection();
      conn.setInstanceFollowRedirects(true);
      conn.setRequestMethod("GET");
      conn.setConnectTimeout(5000);
      conn.setReadTimeout(5000);
      int responseCode = conn.getResponseCode();
      conn.disconnect();
      return responseCode != HttpURLConnection.HTTP_NOT_FOUND;
    } catch (IOException e) {
      return false;
    }
  }

  private static ValidationError invalidUrlError(String column, int row, Object value, String message) {
    return ValidationError.builder()
        .setColumnName(column)
        .setRowNumber(row)
        .setErrorDescription(message)
        .setProp(VALUE, value)
        .setProp(ERROR_TYPE, "invalidUrl")
        .setProp(SEVERITY, 1)
        .build();
  }
}
