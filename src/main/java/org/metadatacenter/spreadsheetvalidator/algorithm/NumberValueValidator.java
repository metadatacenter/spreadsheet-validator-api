package org.metadatacenter.spreadsheetvalidator.algorithm;

import org.metadatacenter.spreadsheetvalidator.RepairClosures;
import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidationResult;
import org.metadatacenter.spreadsheetvalidator.domain.ColumnDescription;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import javax.annotation.Nonnull;

import static org.metadatacenter.spreadsheetvalidator.algorithm.PropNames.ERROR_TYPE;
import static org.metadatacenter.spreadsheetvalidator.algorithm.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.algorithm.PropNames.SUGGESTION;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isString;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NumberValueValidator extends InputValueValidator {
  @Override
  public void validateInputValue(@Nonnull Object value,
                                 @Nonnull String columnName,
                                 @Nonnull Integer rowNumber,
                                 @Nonnull ColumnDescription columnDescription,
                                 @Nonnull RepairClosures repairClosures,
                                 @Nonnull ValidationResult validationResult) {
    var columnType = columnDescription.getColumnType();
    switch (columnType) {
      case DECIMAL:
      case INTEGER:
        if (Assert.that(value, isString())) {
          var repairClosure = repairClosures.get("numberExtractor");
          var suggestion = repairClosure.execute(value);
          validationResult.add(
              ValidationError.builder()
                  .setColumnName(columnName)
                  .setRowNumber(rowNumber)
                  .setInvalidValue(value)
                  .setErrorDescription("Value is not a number")
                  .setOtherProp(SUGGESTION, suggestion)
                  .setOtherProp(ERROR_TYPE, "notNumberType")
                  .setOtherProp(SEVERITY, 1)
                  .build()
          );
        }
        break;
    }
  }
}
