package org.metadatacenter.spreadsheetvalidator;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ValidatorContext {

  private final RepairClosures repairClosures;

  private final ValidationResult validationResult;

  @Inject
  public ValidatorContext(@Nonnull RepairClosures repairClosures,
                          @Nonnull ValidationResult validationResult) {
    this.repairClosures = checkNotNull(repairClosures);
    this.validationResult = checkNotNull(validationResult);
  }

  public void setClosure(@Nonnull String key, @Nonnull Closure closure) {
    repairClosures.add(key, closure);
  }

  public Closure getClosure(@Nonnull String key) {
    return repairClosures.get(key);
  }

  @Nonnull
  public ValidationResult getValidationResult() {
    return validationResult;
  }
}
