package org.metadatacenter.spreadsheetvalidator.algorithm;

import org.metadatacenter.spreadsheetvalidator.RepairClosures;
import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidationResult;
import org.metadatacenter.spreadsheetvalidator.domain.ColumnDescription;

import javax.annotation.Nonnull;

import static org.metadatacenter.spreadsheetvalidator.algorithm.PropNames.ERROR_TYPE;
import static org.metadatacenter.spreadsheetvalidator.algorithm.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.algorithm.PropNames.SUGGESTION;
import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.DECIMAL;
import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.INTEGER;
import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.NUMBER;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class MaxRangeValidator extends InputValueValidator {
  @Override
  public void validateInputValue(@Nonnull Object value,
                                 @Nonnull String columnName,
                                 @Nonnull Integer rowNumber,
                                 @Nonnull ColumnDescription columnDescription,
                                 @Nonnull RepairClosures repairClosures,
                                 @Nonnull ValidationResult validationResult) {
    if (columnDescription.getColumnType() == NUMBER) {
      var numberValue = (Number) value;
      var isOutOfRange = false;
      if (columnDescription.hasMaxValue()) {
        var maxValue = columnDescription.getMaxValue();
        if ((columnDescription.getColumnSubType() == INTEGER
            && numberValue.intValue() < maxValue.intValue())
            || (columnDescription.getColumnSubType() == DECIMAL
            && numberValue.floatValue() < maxValue.floatValue())) {
          validationResult.add(
              ValidationError.builder()
                  .setColumnName(columnName)
                  .setRowNumber(rowNumber)
                  .setInvalidValue(value)
                  .setErrorDescription("Value is exceeded the maximum value constraint of " + maxValue)
                  .setOtherProp(SUGGESTION, null)
                  .setOtherProp(ERROR_TYPE, "numberOutOfRange")
                  .setOtherProp(SEVERITY, 3)
                  .build());
        }
      }
    }
  }
}