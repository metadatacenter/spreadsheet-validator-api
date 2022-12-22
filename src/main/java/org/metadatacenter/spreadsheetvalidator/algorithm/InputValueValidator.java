package org.metadatacenter.spreadsheetvalidator.algorithm;

import org.metadatacenter.spreadsheetvalidator.RepairClosures;
import org.metadatacenter.spreadsheetvalidator.SpreadsheetValidator;
import org.metadatacenter.spreadsheetvalidator.ValidationResult;
import org.metadatacenter.spreadsheetvalidator.Validator;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.domain.ColumnDescription;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetRow;
import org.metadatacenter.spreadsheetvalidator.util.ValueAssertion;

import javax.annotation.Nonnull;
import java.util.List;

import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isNullOrEmpty;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public abstract class InputValueValidator implements Validator {

  @Override
  public void validate(@Nonnull ValidatorContext context,
                       @Nonnull SpreadsheetRow spreadsheetRow) {
    spreadsheetRow.columnStream()
        .forEach(columnName -> {
          var value = spreadsheetRow.getValue(columnName);
          if (!ValueAssertion.equals(value, isNullOrEmpty())) {
            validateInputValue(value,
                columnName,
                spreadsheetRow.getRowNumber(),
                context.getSpreadsheetDefinition().getColumnDescription(columnName),
                context.getRepairClosures(),
                context.getValidationResult());
          }
        });
  }

  public abstract void validateInputValue(@Nonnull Object value,
                                          @Nonnull String columnName,
                                          @Nonnull Integer rowNumber,
                                          @Nonnull ColumnDescription columnDescription,
                                          @Nonnull RepairClosures repairClosures,
                                          @Nonnull ValidationResult validationResult);

  @Override
  public void chain(@Nonnull SpreadsheetValidator spreadsheetValidator,
                    @Nonnull List<SpreadsheetRow> spreadsheetRows) {
    // Override this for a custom implementation
  }
}
