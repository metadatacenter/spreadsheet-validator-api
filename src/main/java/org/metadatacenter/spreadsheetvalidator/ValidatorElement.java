package org.metadatacenter.spreadsheetvalidator;

import com.google.auto.value.AutoValue;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetRow;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ValidatorElement {

  public static ValidatorElement create(@Nonnull Validator validator,
                                        @Nonnull SpreadsheetRow spreadsheetRow) {
    return new AutoValue_ValidatorElement(validator, spreadsheetRow);
  }

  @Nonnull
  public abstract Validator getValidator();

  @Nonnull
  public abstract SpreadsheetRow getSpreadsheetRow();
}
