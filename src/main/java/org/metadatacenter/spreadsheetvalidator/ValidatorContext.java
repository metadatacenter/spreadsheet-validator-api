package org.metadatacenter.spreadsheetvalidator;

import com.google.auto.value.AutoValue;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetDefinition;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ValidatorContext {

  protected static ValidatorContext create(@Nonnull SpreadsheetDefinition spreadsheetDefinition,
                                           @Nonnull RepairClosures repairClosures,
                                           @Nonnull ValidationResult validationResult) {
    return new AutoValue_ValidatorContext(spreadsheetDefinition, repairClosures, validationResult);
  }

  public static ValidatorContext create(@Nonnull SpreadsheetDefinition spreadsheetDefinition) {
    return create(spreadsheetDefinition, new RepairClosures(), new ValidationResult());
  }

  @Nonnull
  public abstract SpreadsheetDefinition getSpreadsheetDefinition();

  @Nonnull
  public abstract RepairClosures getRepairClosures();

  @Nonnull
  public abstract ValidationResult getValidationResult();
}
