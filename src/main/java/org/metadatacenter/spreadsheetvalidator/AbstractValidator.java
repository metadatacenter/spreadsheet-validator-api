package org.metadatacenter.spreadsheetvalidator;

import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetRow;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public abstract class AbstractValidator implements Validator {

  @Override
  public void chain(@Nonnull SpreadsheetValidator spreadsheetValidator,
                    @Nonnull SpreadsheetRow spreadsheetRow) {
    // Override this for a custom implementation
  }
}
