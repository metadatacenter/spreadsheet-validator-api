package org.metadatacenter.spreadsheetvalidator.validator;

import org.jetbrains.annotations.NotNull;
import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.NUMBER;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isNumber;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.not;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ERROR_TYPE;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SUGGESTION;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NumberTypeValidator extends InputValueValidator {
  @Override
  public void validateInputValue(@NotNull Object value,
                                 @NotNull ValueContext valueContext,
                                 @NotNull ValidatorContext validatorContext) {
    var columnType = valueContext.getColumnDescription().getColumnType();
    if (columnType == NUMBER && Assert.that(value, not(isNumber()))) {
      var closure = validatorContext.getClosure("numberExtractor");
      var suggestion = closure.execute(value);
      validatorContext.getValidationResult().add(
          ValidationError.builder()
              .setColumnName(valueContext.getColumn())
              .setRowNumber(valueContext.getRow())
              .setInvalidValue(value)
              .setErrorDescription("Value is not a number")
              .setOtherProp(SUGGESTION, suggestion)
              .setOtherProp(ERROR_TYPE, "notNumberType")
              .setOtherProp(SEVERITY, 1)
              .build()
      );
    }
  }
}
