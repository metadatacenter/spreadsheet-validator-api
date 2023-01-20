package org.metadatacenter.spreadsheetvalidator.validator;

import com.google.common.collect.Lists;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetRow;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetSchema;

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
      throw new BadRequestException(
          String.format("Additional columns are not allowed (%s were unexpected)", additionalColumns));
    }
    return spreadsheetRow;
  }
}
