package org.metadatacenter.spreadsheetvalidator.validator;

import org.metadatacenter.spreadsheetvalidator.RepairClosures;
import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidationResult;
import org.metadatacenter.spreadsheetvalidator.domain.ColumnDescription;

import javax.annotation.Nonnull;

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
                                 @Nonnull String columnName,
                                 @Nonnull Integer rowNumber,
                                 @Nonnull ColumnDescription columnDescription,
                                 @Nonnull RepairClosures repairClosures,
                                 @Nonnull ValidationResult validationResult) {
    if (columnDescription.getColumnType() == NUMBER) {
      var numberValue = (Number) value;
      if (columnDescription.hasMinValue()) {
        var minValue = columnDescription.getMinValue();
        if ((columnDescription.getColumnSubType() == INTEGER
            && numberValue.intValue() > minValue.intValue())
            || (columnDescription.getColumnSubType() == DECIMAL
            && numberValue.floatValue() > minValue.floatValue())) {
          validationResult.add(
              ValidationError.builder()
                  .setColumnName(columnName)
                  .setRowNumber(rowNumber)
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