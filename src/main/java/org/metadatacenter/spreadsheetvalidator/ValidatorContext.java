package org.metadatacenter.spreadsheetvalidator;

import org.metadatacenter.spreadsheetvalidator.validator.closure.Closure;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ValidatorContext {

  private final RepairClosures repairClosures;

  private final ValidationSettings validationSettings;

  @Inject
  public ValidatorContext(@Nonnull RepairClosures repairClosures,
                          @Nonnull ValidationSettings validationSettings) {
    this.repairClosures = checkNotNull(repairClosures);
    this.validationSettings = checkNotNull(validationSettings);
  }

  @Nonnull
  public Closure getClosure(@Nonnull String key) {
    return repairClosures.get(key);
  }

  @Nonnull
  public ValidationSettings getValidationSettings() {
    return validationSettings;
  }
}
