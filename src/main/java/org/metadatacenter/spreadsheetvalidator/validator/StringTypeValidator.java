package org.metadatacenter.spreadsheetvalidator.validator;

import org.jetbrains.annotations.NotNull;
import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.STRING;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isString;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.not;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ERROR_TYPE;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.VALUE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class StringTypeValidator extends InputValueValidator {
  @Override
  public void validateInputValue(@NotNull Object value,
                                 @NotNull ValueContext valueContext,
                                 @NotNull ValidatorContext validatorContext) {
    var valueType = valueContext.getColumnDescription().getColumnType();
    if (valueType == STRING && Assert.that(value, not(isString()))) {
      validatorContext.getValidationResult().add(
          ValidationError.builder()
              .setColumnName(valueContext.getColumn())
              .setRowNumber(valueContext.getRow())
              .setErrorDescription("Value is not a string")
              .setProp(VALUE, value)
              .setProp(ERROR_TYPE, "notStringType")
              .setProp(SEVERITY, 1)
              .build()
      );
    }
  }
}
