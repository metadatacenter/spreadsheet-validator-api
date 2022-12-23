package org.metadatacenter.spreadsheetvalidator.validator;

import org.metadatacenter.spreadsheetvalidator.SpreadsheetValidator;
import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.Validator;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetRow;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import javax.annotation.Nonnull;
import java.util.List;

import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ERROR_TYPE;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isNullOrEmpty;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class RequiredFieldValidator implements Validator {

  @Override
  public void validate(@Nonnull ValidatorContext context,
                       @Nonnull SpreadsheetRow spreadsheetRow) {
    var spreadsheetDefinition = context.getSpreadsheetDefinition();
    var validationResult = context.getValidationResult();
    spreadsheetRow.columnStream()
        .forEach(columnName -> {
          var columnDescription = spreadsheetDefinition.getColumnDescription(columnName);
          if (columnDescription.isRequiredColumn()) {
            var rowNumber = spreadsheetRow.getRowNumber();
            var value = spreadsheetRow.getValue(columnName);
            if (Assert.that(value, isNullOrEmpty())) {
              validationResult.add(
                  ValidationError.builder()
                      .setColumnName(columnName)
                      .setRowNumber(rowNumber)
                      .setInvalidValue(value)
                      .setErrorDescription("Required value is missing")
                      .setOtherProp(ERROR_TYPE, "missingRequired")
                      .setOtherProp(SEVERITY, 5)
                      .build());
            }
          }
        });
  }

  @Override
  public void chain(@Nonnull SpreadsheetValidator spreadsheetValidator,
                    @Nonnull List<SpreadsheetRow> spreadsheetRows) {
    // No implementation
  }
}
