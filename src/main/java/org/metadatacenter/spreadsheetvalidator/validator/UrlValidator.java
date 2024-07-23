package org.metadatacenter.spreadsheetvalidator.validator;

import com.google.common.collect.ImmutableMap;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.URL;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.COLUMN_LABEL;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.COLUMN_NAME;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ERROR_MESSAGE;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ERROR_TYPE;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ROW_INDEX;
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
    var columnDescription = valueContext.getColumnDescription();
    var valueType = columnDescription.getColumnType();
    if (valueType == URL) {
      try {
        var url = new URL(String.valueOf(value));
        if (!isResolvable(url)) {
          validatorContext.getValidationResult().add(
              ImmutableMap.of(
                  ROW_INDEX, valueContext.getRow(),
                  COLUMN_NAME, valueContext.getColumn(),
                  COLUMN_LABEL, columnDescription.getColumnLabel(),
                  VALUE, value,
                  ERROR_TYPE, "invalidUrl",
                  ERROR_MESSAGE, "URL does not exist",
                  SEVERITY, 1
              ));
        }
      } catch (MalformedURLException e) {
        validatorContext.getValidationResult().add(
            ImmutableMap.of(
                ROW_INDEX, valueContext.getRow(),
                COLUMN_NAME, valueContext.getColumn(),
                COLUMN_LABEL, columnDescription.getColumnLabel(),
                VALUE, value,
                ERROR_TYPE, "invalidUrl",
                ERROR_MESSAGE, "URL is not valid",
                SEVERITY, 1
            ));
      }
    }
  }

  private boolean isResolvable(URL url) {
    try {
      var conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("HEAD");
      int responseCode = conn.getResponseCode();
      return responseCode != HttpURLConnection.HTTP_NOT_FOUND;
    } catch (IOException e) {
      return false;
    }
  }
}
