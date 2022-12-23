package org.metadatacenter.spreadsheetvalidator.validator;

import org.metadatacenter.spreadsheetvalidator.RepairClosures;
import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidationResult;
import org.metadatacenter.spreadsheetvalidator.domain.ColumnDescription;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import javax.annotation.Nonnull;

import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ERROR_TYPE;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SUGGESTION;
import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.NUMBER;
import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.STRING;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isNumber;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isString;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.not;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ValueTypeValidator extends InputValueValidator {
  @Override
  public void validateInputValue(@Nonnull Object value,
                                 @Nonnull String columnName,
                                 @Nonnull Integer rowNumber,
                                 @Nonnull ColumnDescription columnDescription,
                                 @Nonnull RepairClosures repairClosures,
                                 @Nonnull ValidationResult validationResult) {
    var valueType = columnDescription.getColumnType();
    if (valueType == STRING && Assert.that(value, not(isString()))) {
      validationResult.add(
          ValidationError.builder()
              .setColumnName(columnName)
              .setRowNumber(rowNumber)
              .setInvalidValue(value)
              .setErrorDescription("Value is not a string")
              .setOtherProp(ERROR_TYPE, "notStringType")
              .setOtherProp(SEVERITY, 1)
              .build()
      );
    } else if (valueType == NUMBER && Assert.that(value, not(isNumber()))) {
      var numberExtractor = repairClosures.get("numberExtractor");
      var suggestion = numberExtractor.execute(value);
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
  }
}
