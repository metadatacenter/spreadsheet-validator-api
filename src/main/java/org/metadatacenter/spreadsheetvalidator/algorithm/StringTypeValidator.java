package org.metadatacenter.spreadsheetvalidator.algorithm;

import org.metadatacenter.spreadsheetvalidator.AbstractValidator;
import org.metadatacenter.spreadsheetvalidator.RepairClosures;
import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidationResult;
import org.metadatacenter.spreadsheetvalidator.domain.ColumnDescription;
import org.metadatacenter.spreadsheetvalidator.domain.ValueType;
import org.metadatacenter.spreadsheetvalidator.util.ValueAssertion;

import javax.annotation.Nonnull;

import static org.metadatacenter.spreadsheetvalidator.algorithm.PropNames.ERROR_TYPE;
import static org.metadatacenter.spreadsheetvalidator.algorithm.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.STRING;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isString;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class StringTypeValidator extends AbstractValidator {
  @Override
  public boolean validateOnEach(@Nonnull Integer rowNumber,
                                @Nonnull String columnName,
                                @Nonnull Object value,
                                @Nonnull ColumnDescription columnDescription,
                                @Nonnull RepairClosures repairClosures,
                                @Nonnull ValidationResult validationResult) {
    if (columnDescription.getColumnType() == STRING) {
      if (!ValueAssertion.equals(value, isString())) {
        validationResult.add(
            ValidationError.builder()
                .setRowNumber(rowNumber)
                .setColumnName(columnName)
                .setInvalidValue(value)
                .setErrorDescription("Value is not a string")
                .setOtherProp(ERROR_TYPE, "notStringType")
                .setOtherProp(SEVERITY, 1)
                .build()
        );
        return false;
      }
    }
    return true;
  }
}
