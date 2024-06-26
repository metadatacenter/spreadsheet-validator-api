package org.metadatacenter.spreadsheetvalidator.validator;

import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import javax.annotation.Nonnull;

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
public class IntegerNumberRangeValidator extends InputValueValidator {
  @Override
  public void validateInputValue(@Nonnull Object value,
                                 @Nonnull ValueContext valueContext,
                                 @Nonnull ValidatorContext validatorContext) {
    var columnDescription = valueContext.getColumnDescription();
    var columnSubType = columnDescription.getColumnSubType();
    if (columnSubType == INTEGER && Assert.that(value, isNumber())) {
      var suggestion = "";
      if (columnDescription.hasMinValue() || columnDescription.hasMaxValue()) {
        var numberValue = ((Number) value).intValue();
        if (columnDescription.hasMinValue() && columnDescription.hasMaxValue()) {
          var minValue = columnDescription.getMinValue().intValue();
          var maxValue = columnDescription.getMaxValue().intValue();
          if (numberValue < minValue || numberValue > maxValue) {
            suggestion = String.format("Enter a number between %d and %d", minValue, maxValue);
          }
        } else if (columnDescription.hasMinValue() && !columnDescription.hasMinValue()) {
          var minValue = columnDescription.getMinValue().intValue();
          if (numberValue < minValue) {
            suggestion = String.format("Enter a number above %d", minValue);
          }
        } else if (!columnDescription.hasMinValue() && columnDescription.hasMaxValue()) {
          var maxValue = columnDescription.getMaxValue().intValue();
          if (numberValue > maxValue) {
            suggestion = String.format("Enter a number below %d", maxValue);
          }
        }
      }
      if (!suggestion.isEmpty()) {
        // Construct the error message
        validatorContext.getValidationResult().add(
            ValidationError.builder()
                .setColumnName(valueContext.getColumn())
                .setRowNumber(valueContext.getRow())
                .setErrorDescription("Integer number is out of range")
                .setProp(VALUE, value)
                .setProp(ERROR_TYPE, "numberOutOfRange")
                .setProp(SUGGESTION, suggestion)
                .setProp(SEVERITY, 3)
                .build());
      }
    }
  }
}
