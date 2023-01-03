package org.metadatacenter.spreadsheetvalidator;

import com.google.auto.value.AutoValue;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetSchema;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ValidatorContext {

  protected static ValidatorContext create(@Nonnull SpreadsheetSchema spreadsheetSchema,
                                           @Nonnull RepairClosures repairClosures,
                                           @Nonnull ValidationResult validationResult) {
    return new AutoValue_ValidatorContext(spreadsheetSchema, repairClosures, validationResult);
  }

  public static ValidatorContext create(@Nonnull SpreadsheetSchema spreadsheetSchema) {
    return create(spreadsheetSchema, new RepairClosures(), new ValidationResult());
  }

  @Nonnull
  public abstract SpreadsheetSchema getSpreadsheetDefinition();

  @Nonnull
  public abstract RepairClosures getRepairClosures();

  @Nonnull
  public abstract ValidationResult getValidationResult();
}
