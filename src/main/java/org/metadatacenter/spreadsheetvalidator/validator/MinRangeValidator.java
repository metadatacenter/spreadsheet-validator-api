package org.metadatacenter.spreadsheetvalidator.validator;

import org.metadatacenter.spreadsheetvalidator.RepairClosures;
import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidationResult;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.domain.ColumnDescription;
import org.metadatacenter.spreadsheetvalidator.domain.ValueType;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import javax.annotation.Nonnull;

import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isNumber;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.not;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ERROR_TYPE;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SUGGESTION;
import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.DECIMAL;
import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.INTEGER;
import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.NUMBER;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class MinRangeValidator extends InputValueValidator {
  @Override
  public void validateInputValue(@Nonnull Object value,
                                 @Nonnull ValueContext valueContext,
                                 @Nonnull ValidatorContext validatorContext) {
    var columnDescription = valueContext.getColumnDescription();
    var columnType = columnDescription.getColumnType();
    if (columnType == NUMBER && Assert.that(value, isNumber())) {
      var numberValue = (Number) value;
      if (columnDescription.hasMinValue()) {
        var minValue = columnDescription.getMinValue();
        var columnSubType = columnDescription.getColumnSubType();
        if ((columnSubType == INTEGER && numberValue.intValue() > minValue.intValue())
            || (columnSubType == DECIMAL && numberValue.floatValue() > minValue.floatValue())) {
          validatorContext.getValidationResult().add(
              ValidationError.builder()
                  .setColumnName(valueContext.getColumn())
                  .setRowNumber(valueContext.getRow())
                  .setInvalidValue(value)
                  .setErrorDescription("Value is exceeded the minimum value constraint of " + minValue)
                  .setOtherProp(SUGGESTION, null)
                  .setOtherProp(ERROR_TYPE, "numberOutOfRange")
                  .setOtherProp(SEVERITY, 3)
                  .build());
        }
      }
    }
  }
}