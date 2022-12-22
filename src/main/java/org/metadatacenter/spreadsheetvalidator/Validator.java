package org.metadatacenter.spreadsheetvalidator;

import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetRow;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface Validator {

  void validate(@Nonnull ValidatorContext context,
                @Nonnull SpreadsheetRow spreadsheetRow);

  void chain(@Nonnull SpreadsheetValidator spreadsheetValidator,
             @Nonnull List<SpreadsheetRow> spreadsheetRows);
}
