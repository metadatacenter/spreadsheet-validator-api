package org.metadatacenter.spreadsheetvalidator.validator;

import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.Validator;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetRow;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetSchema;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import javax.annotation.Nonnull;

import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isNullOrEmpty;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ERROR_TYPE;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.VALUE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class RequiredFieldValidator implements Validator {
  @Override
  public void validate(@Nonnull ValidatorContext context,
                       @Nonnull SpreadsheetSchema spreadsheetSchema,
                       @Nonnull SpreadsheetRow spreadsheetRow) {
    var validationResult = context.getValidationResult();
    spreadsheetRow.columnStream()
        .forEach(column -> {
          if (spreadsheetSchema.containsColumn(column)) {
            var columnDescription = spreadsheetSchema.getColumnDescription(column);
            var rowNumber = spreadsheetRow.getRowNumber();
            var value = spreadsheetRow.getValue(column);
            if (columnDescription.isRequiredColumn() && Assert.that(value, isNullOrEmpty())) {
              validationResult.add(
                  ValidationError.builder()
                      .setColumnName(column)
                      .setRowNumber(rowNumber)
                      .setErrorDescription("Required value is missing")
                      .setProp(VALUE, value)
                      .setProp(ERROR_TYPE, "missingRequired")
                      .setProp(SEVERITY, 5)
                      .build());
            }
          }
        });
  }
}
