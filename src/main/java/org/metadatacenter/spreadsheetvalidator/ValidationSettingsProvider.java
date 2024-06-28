package org.metadatacenter.spreadsheetvalidator;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ValidationSettingsProvider {

  private final GeneralConfig generalConfig;

  public ValidationSettingsProvider(@Nonnull GeneralConfig generalConfig) {
    this.generalConfig = checkNotNull(generalConfig);
  }

  public ValidationSettings get() {
    return new ValidationSettings(generalConfig);
  }
}
