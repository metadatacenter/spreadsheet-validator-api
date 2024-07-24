package org.metadatacenter.spreadsheetvalidator.validator;

import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import javax.annotation.Nonnull;
import java.util.Optional;

import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.INTEGER;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isNumber;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.COLUMN_LABEL;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SUGGESTION;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.VALUE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class IntegerNumberRangeValidator extends InputValueValidator {

  @Override
  public Optional<ValidationError> validateInputValue(@Nonnull Object value,
                                                      @Nonnull ValueContext valueContext,
                                                      @Nonnull ValidatorContext validatorContext) {
    var columnDescription = valueContext.getColumnDescription();
    var columnSubType = columnDescription.getColumnSubType();

    if (columnSubType == INTEGER && Assert.that(value, isNumber())) {
      var numberValue = ((Number) value).intValue();

      var hasMinValue = columnDescription.hasMinValue();
      var hasMaxValue = columnDescription.hasMaxValue();
      var minValue = hasMinValue ? columnDescription.getMinValue().intValue() : Integer.MIN_VALUE;
      var maxValue = hasMaxValue ? columnDescription.getMaxValue().intValue() : Integer.MAX_VALUE;

      if (numberValue < minValue || numberValue > maxValue) {
        var suggestion = "";
        if (hasMinValue && hasMaxValue) {
          suggestion = String.format("Enter an integer number between %f and %f", minValue, maxValue);
        } else if (hasMinValue) {
          suggestion = String.format("Enter an integer number above %f", minValue);
        } else if (hasMaxValue) {
          suggestion = String.format("Enter an integer number below %f", maxValue);
        }
        var validationError = ValidationError.builder()
            .setErrorType("numberOutOfRange")
            .setErrorMessage("Integer number is out of range")
            .setErrorLocation(valueContext.getColumn(), valueContext.getRow())
            .setOtherProp(VALUE, value)
            .setOtherProp(COLUMN_LABEL, columnDescription.getColumnLabel())
            .setOtherProp(SEVERITY, 3)
            .setOtherProp(SUGGESTION, suggestion)
            .build();
        return Optional.of(validationError);
      }
    }
    return Optional.empty();
  }
}
