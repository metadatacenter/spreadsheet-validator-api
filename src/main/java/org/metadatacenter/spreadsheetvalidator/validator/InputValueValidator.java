package org.metadatacenter.spreadsheetvalidator.validator;

import org.metadatacenter.spreadsheetvalidator.Validator;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetRow;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetSchema;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import javax.annotation.Nonnull;

import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isNullOrEmpty;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.not;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public abstract class InputValueValidator implements Validator {

  @Override
  public void validate(@Nonnull ValidatorContext validatorContext,
                       @Nonnull SpreadsheetSchema spreadsheetSchema,
                       @Nonnull SpreadsheetRow spreadsheetRow) {
    spreadsheetRow.columnStream()
        .forEach(column -> {
          var value = spreadsheetRow.getValue(column);
          if (Assert.that(value, not(isNullOrEmpty()))) {
            var valueContext = ValueContext.create(
                column,
                spreadsheetRow.getRowNumber(),
                spreadsheetSchema.getColumnDescription(column));
            validateInputValue(value, valueContext, validatorContext);
          }
        });
  }

  public abstract void validateInputValue(@Nonnull Object value,
                                          @Nonnull ValueContext valueContext,
                                          @Nonnull ValidatorContext validatorContext);
}
