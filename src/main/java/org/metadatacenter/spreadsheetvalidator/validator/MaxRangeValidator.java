package org.metadatacenter.spreadsheetvalidator.validator;

import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import javax.annotation.Nonnull;

import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.DECIMAL;
import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.INTEGER;
import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.NUMBER;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isNumber;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ERROR_TYPE;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.VALUE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class MaxRangeValidator extends InputValueValidator {
  @Override
  public void validateInputValue(@Nonnull Object value,
                                 @Nonnull ValueContext valueContext,
                                 @Nonnull ValidatorContext validatorContext) {
    var columnDescription = valueContext.getColumnDescription();
    var columnType = columnDescription.getColumnType();
    if (columnType == NUMBER && Assert.that(value, isNumber())) {
      var numberValue = (Number) value;
      if (columnDescription.hasMaxValue()) {
        var maxValue = columnDescription.getMaxValue();
        var columnSubType = columnDescription.getColumnSubType();
        if ((columnSubType == INTEGER && numberValue.intValue() < maxValue.intValue())
            || (columnSubType == DECIMAL && numberValue.floatValue() < maxValue.floatValue())) {
          validatorContext.getValidationResult().add(
              ValidationError.builder()
                  .setColumnName(valueContext.getColumn())
                  .setRowNumber(valueContext.getRow())
                  .setErrorDescription("Value is exceeded the maximum value constraint of " + maxValue)
                  .setProp(VALUE, value)
                  .setProp(ERROR_TYPE, "numberOutOfRange")
                  .setProp(SEVERITY, 3)
                  .build());
        }
      }
    }
  }
}