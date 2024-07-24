package org.metadatacenter.spreadsheetvalidator.validator;

import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.URL;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.COLUMN_LABEL;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.VALUE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class UrlValidator extends InputValueValidator {

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
          return Optional.of(createValidationError(value, valueContext, "URL does not exist"));
        }
      } catch (MalformedURLException e) {
        return Optional.of(createValidationError(value, valueContext, "URL is not valid"));
      }
    }
    return Optional.empty();
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
