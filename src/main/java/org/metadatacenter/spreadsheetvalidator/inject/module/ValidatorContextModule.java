package org.metadatacenter.spreadsheetvalidator.inject.module;

import dagger.Module;
import dagger.Provides;
import org.metadatacenter.spreadsheetvalidator.RepairClosures;
import org.metadatacenter.spreadsheetvalidator.ValidationResult;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class ValidatorContextModule {

  @Provides
  public RepairClosures providesRepairClosures() {
    return new RepairClosures();
  }

  @Provides
  public ValidationResult providesValidationResults() {
    return new ValidationResult();
  }
}
