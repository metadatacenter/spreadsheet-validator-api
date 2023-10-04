package org.metadatacenter.spreadsheetvalidator.validator;

import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;

import javax.annotation.Nonnull;

import java.util.regex.Pattern;

import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.STRING;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ERROR_TYPE;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.VALUE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class StringPatternValidator extends InputValueValidator {
  @Override
  public void validateInputValue(@Nonnull Object value,
                                 @Nonnull ValueContext valueContext,
                                 @Nonnull ValidatorContext validatorContext) {
    var columnDescription = valueContext.getColumnDescription();
    var valueType = columnDescription.getColumnType();
    if (valueType == STRING && columnDescription.hasRegexString()) {
      var regexString = columnDescription.getRegexString();
      var pattern = Pattern.compile(regexString);
      var matcher = pattern.matcher(String.valueOf(value));
      if (!matcher.matches()) {
        validatorContext.getValidationResult().add(
            ValidationError.builder()
                .setColumnName(valueContext.getColumn())
                .setRowNumber(valueContext.getRow())
                .setErrorDescription("Value doesn't match the required pattern")
                .setProp(VALUE, value)
                .setProp(ERROR_TYPE, "invalidValueFormat")
                .setProp(SEVERITY, 1)
                .build());
      }
    }
  }
}
