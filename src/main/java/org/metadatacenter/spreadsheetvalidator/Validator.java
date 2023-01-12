package org.metadatacenter.spreadsheetvalidator;

import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetRow;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetSchema;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface Validator {

  void validate(@Nonnull ValidatorContext context,
                @Nonnull SpreadsheetSchema spreadsheetSchema,
                @Nonnull SpreadsheetRow spreadsheetRow);
}
