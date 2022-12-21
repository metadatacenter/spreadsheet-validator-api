package org.metadatacenter.spreadsheetvalidator;

import org.metadatacenter.spreadsheetvalidator.domain.ColumnDescription;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetDefinition;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetRow;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public abstract class AbstractValidator implements Validator {

  @Override
  public boolean validate (@Nonnull ValidatorContext context,
                           @Nonnull SpreadsheetRow spreadsheetRow) {
    return spreadsheetRow.columnStream()
        .map(columnName -> validateOnEach(
            spreadsheetRow.getRowNumber(),
            columnName,
            spreadsheetRow.getValue(columnName),
            context.getSpreadsheetDefinition().getColumnDescription(columnName),
            context.getRepairClosures(),
            context.getValidationResult()))
        .allMatch(v -> v);
  }

  @Override
  public void chain(@Nonnull SpreadsheetValidator spreadsheetValidator,
                    @Nonnull SpreadsheetRow spreadsheetRow) {
    // Override this for a custom implementation
  }

  public abstract boolean validateOnEach(@Nonnull Integer rowNumber,
                                         @Nonnull String columnName,
                                         @Nonnull Object value,
                                         @Nonnull ColumnDescription columnDescription,
                                         @Nonnull RepairClosures repairClosures,
                                         @Nonnull ValidationResult validationResult);
}
