package org.metadatacenter.spreadsheetvalidator.validator;

import com.google.common.collect.Lists;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetRow;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetSchema;
import org.metadatacenter.spreadsheetvalidator.validator.exception.UnexpectedColumnsException;

import javax.annotation.Nonnull;
import javax.ws.rs.BadRequestException;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ValidatorUtils {

  public static SpreadsheetRow checkAdditionalColumns(@Nonnull SpreadsheetRow spreadsheetRow,
                                                      @Nonnull SpreadsheetSchema spreadsheetSchema) {
    var additionalColumns = Lists.<String>newArrayList();
    spreadsheetRow.columnStream()
        .forEach(column -> {
          if (!spreadsheetSchema.containsColumn(column)) {
            additionalColumns.add(column);
          }});
    if (!additionalColumns.isEmpty()) {
      throw new UnexpectedColumnsException(additionalColumns, spreadsheetSchema.getColumnOrder());
    }
    return spreadsheetRow;
  }
}
