package org.metadatacenter.spreadsheetvalidator;

import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetRow;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetSchema;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface Validator {

  List<ValidationError> validate(@Nonnull SpreadsheetRow spreadsheetRow,
                                 @Nonnull SpreadsheetSchema spreadsheetSchema,
                                 @Nonnull ValidatorContext context);
}
