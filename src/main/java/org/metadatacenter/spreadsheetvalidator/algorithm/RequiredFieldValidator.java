package org.metadatacenter.spreadsheetvalidator.algorithm;

import org.metadatacenter.spreadsheetvalidator.AbstractValidator;
import org.metadatacenter.spreadsheetvalidator.RepairClosures;
import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidationResult;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.domain.ColumnDescription;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetRow;
import org.metadatacenter.spreadsheetvalidator.util.ValueAssertion;

import javax.annotation.Nonnull;

import static org.metadatacenter.spreadsheetvalidator.algorithm.PropNames.ERROR_TYPE;
import static org.metadatacenter.spreadsheetvalidator.algorithm.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isNullOrEmpty;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class RequiredFieldValidator extends AbstractValidator {

  @Override
  public boolean validateOnEach(@Nonnull Integer rowNumber,
                                @Nonnull String columnName,
                                @Nonnull Object value,
                                @Nonnull ColumnDescription columnDescription,
                                @Nonnull RepairClosures repairClosures,
                                @Nonnull ValidationResult validationResult) {
    if (columnDescription.isRequiredColumn()) {
      if (ValueAssertion.equals(value, isNullOrEmpty())) {
        validationResult.add(
            ValidationError.builder()
                .setRowNumber(rowNumber)
                .setColumnName(columnName)
                .setInvalidValue(value)
                .setErrorDescription("Required value is missing")
                .setOtherProp(ERROR_TYPE, "missingRequired")
                .setOtherProp(SEVERITY, 5)
                .build());
        return false;
      }
    }
    return true;
  }
}
