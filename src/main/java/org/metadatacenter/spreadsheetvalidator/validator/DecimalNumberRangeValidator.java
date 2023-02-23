package org.metadatacenter.spreadsheetvalidator.validator;

import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import javax.annotation.Nonnull;

import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.DECIMAL;
import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.INTEGER;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isNumber;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ERROR_TYPE;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SUGGESTION;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.VALUE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DecimalNumberRangeValidator extends InputValueValidator {
  @Override
  public void validateInputValue(@Nonnull Object value,
                                 @Nonnull ValueContext valueContext,
                                 @Nonnull ValidatorContext validatorContext) {
    var columnDescription = valueContext.getColumnDescription();
    var columnSubType = columnDescription.getColumnSubType();
    if (columnSubType == DECIMAL && Assert.that(value, isNumber())) {
      if (columnDescription.hasMinValue() || columnDescription.hasMaxValue()) {
        var suggestion = "";
        var numberValue = ((Number) value).doubleValue();
        if (columnDescription.hasMinValue() && columnDescription.hasMaxValue()) {
          var minValue = columnDescription.getMinValue().doubleValue();
          var maxValue = columnDescription.getMaxValue().doubleValue();
          if (numberValue < minValue || numberValue > maxValue) {
            suggestion = String.format("Enter a decimal number between %f and %f", minValue, maxValue);
          }
        } else if (columnDescription.hasMinValue() && !columnDescription.hasMinValue()) {
          var minValue = columnDescription.getMinValue().doubleValue();
          if (numberValue < minValue) {
            suggestion = String.format("Enter a decimal number above %f", minValue);
          }
        } else if (!columnDescription.hasMinValue() && columnDescription.hasMaxValue()) {
          var maxValue = columnDescription.getMaxValue().doubleValue();
          if (numberValue > maxValue) {
            suggestion = String.format("Enter a decimal number below %f", maxValue);
          }
        }
        // Construct the error message
        validatorContext.getValidationResult().add(
            ValidationError.builder()
                .setColumnName(valueContext.getColumn())
                .setRowNumber(valueContext.getRow())
                .setErrorDescription("Decimal number is out of range")
                .setProp(VALUE, value)
                .setProp(ERROR_TYPE, "numberOutOfRange")
                .setProp(SUGGESTION, suggestion)
                .setProp(SEVERITY, 3)
                .build());
      }
    }
  }
}
