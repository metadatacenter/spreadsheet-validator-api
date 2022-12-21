package org.metadatacenter.spreadsheetvalidator.algorithm;

import org.metadatacenter.spreadsheetvalidator.AbstractValidator;
import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetRow;
import org.metadatacenter.spreadsheetvalidator.util.ValueAssertion;

import javax.annotation.Nonnull;

import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isNullOrEmpty;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class RequiredFieldValidator extends AbstractValidator {
  @Override
  public boolean validate(@Nonnull ValidatorContext context, @Nonnull SpreadsheetRow spreadsheetRow) {
    var spreadsheetDefinition = context.getSpreadsheetDefinition();
    return spreadsheetRow.columnStream()
        .map(columnName -> {
          var columnDescription = spreadsheetDefinition.getColumnDescription(columnName);
          if (columnDescription.isRequiredColumn()) {
            var value = spreadsheetRow.getValue(columnName);
            if (ValueAssertion.equals(value, isNullOrEmpty())) {
              context.getValidationResult().add(
                  ValidationError.builder()
                      .setRowNumber(spreadsheetRow.getRowNumber())
                      .setColumnName(columnName)
                      .setInvalidValue(value)
                      .setErrorDescription("Required value is missing")
                      .setOtherProp("severeLevel", 5)
                      .setOtherProp("errorType", "missingRequired")
                      .build());
              return false;
            }
          }
          return true;
        })
        .allMatch(x -> x);
  }
}
