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

  private final ValidationResultAccumulator validationResultAccumulator;

  @Inject
  public ValidatorContext(@Nonnull RepairClosures repairClosures,
                          @Nonnull ValidationSettings validationSettings,
                          @Nonnull ValidationResultAccumulator validationResultAccumulator) {
    this.repairClosures = checkNotNull(repairClosures);
    this.validationSettings = checkNotNull(validationSettings);
    this.validationResultAccumulator = checkNotNull(validationResultAccumulator);
  }

  @Nonnull
  public Closure getClosure(@Nonnull String key) {
    return repairClosures.get(key);
  }

  @Nonnull
  public ValidationSettings getValidationSettings() {
    return validationSettings;
  }

  @Nonnull
  public ValidationResultAccumulator getValidationResultAccumulator() {
    return validationResultAccumulator;
  }
}
