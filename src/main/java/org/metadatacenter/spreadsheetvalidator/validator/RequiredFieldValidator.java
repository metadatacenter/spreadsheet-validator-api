package org.metadatacenter.spreadsheetvalidator.validator;

import com.google.common.collect.ImmutableMap;
import org.metadatacenter.spreadsheetvalidator.Validator;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetRow;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetSchema;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import javax.annotation.Nonnull;

import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isNullOrEmpty;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.COLUMN_LABEL;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.COLUMN_NAME;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ERROR_MESSAGE;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ERROR_TYPE;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ROW_INDEX;
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
    var validationResult = context.getValidationResultAccumulator();
    spreadsheetRow.columnStream()
        .forEach(columnName -> {
          if (spreadsheetSchema.containsColumn(columnName)) {
            var columnDescription = spreadsheetSchema.getColumnDescription(columnName);
            var rowNumber = spreadsheetRow.getRowNumber();
            var value = spreadsheetRow.getValue(columnName);
            if (columnDescription.isRequiredColumn() && Assert.that(value, isNullOrEmpty())) {
              var valueContext = ValueContext.create(columnName, rowNumber, columnDescription);
              validationResult.add(
                  ImmutableMap.of(
                      ROW_INDEX, valueContext.getRow(),
                      COLUMN_NAME, valueContext.getColumn(),
                      COLUMN_LABEL, columnDescription.getColumnLabel(),
                      VALUE, value,
                      ERROR_TYPE, "missingRequired",
                      ERROR_MESSAGE, "Required value is missing",
                      SEVERITY, 5
                  ));
            }
          }
        });
  }
}
