package org.metadatacenter.spreadsheetvalidator.validator;

import com.google.common.collect.ImmutableMap;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import javax.annotation.Nonnull;

import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.INTEGER;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isNumber;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.COLUMN_LABEL;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.COLUMN_NAME;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ERROR_MESSAGE;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ERROR_TYPE;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ROW_INDEX;
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
            ImmutableMap.of(
                ROW_INDEX, valueContext.getRow(),
                COLUMN_NAME, valueContext.getColumn(),
                COLUMN_LABEL, columnDescription.getColumnLabel(),
                VALUE, value,
                ERROR_TYPE, "numberOutOfRange",
                ERROR_MESSAGE, "Integer number is out of range",
                SUGGESTION, suggestion,
                SEVERITY, 3
            ));
      }
    }
  }
}
