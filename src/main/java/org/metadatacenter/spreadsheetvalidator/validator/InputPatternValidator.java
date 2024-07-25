package org.metadatacenter.spreadsheetvalidator.validator;

import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.COLUMN_LABEL;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.VALUE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class InputPatternValidator extends InputValueValidator {

  @Override
  public Optional<ValidationError> validateInputValue(@Nonnull Object value,
                                                      @Nonnull ValueContext valueContext,
                                                      @Nonnull ValidatorContext validatorContext) {
    var columnDescription = valueContext.getColumnDescription();
    if (columnDescription.hasRegexString()) {
      var regexString = columnDescription.getRegexString();
      var pattern = Pattern.compile(regexString);
      var matcher = pattern.matcher(String.valueOf(value));
      if (!matcher.matches()) {
        var validationError = ValidationError.builder()
            .setErrorType("invalidValueFormat")
            .setErrorMessage("Value does not conform to the expected format: " + regexString)
            .setErrorLocation(valueContext.getColumn(), valueContext.getRow())
            .setOtherProp(VALUE, value)
            .setOtherProp(COLUMN_LABEL, columnDescription.getColumnLabel())
            .setOtherProp(SEVERITY, 1)
            .build();
        return Optional.of(validationError);
      }
    }
    return Optional.empty();
  }
}
