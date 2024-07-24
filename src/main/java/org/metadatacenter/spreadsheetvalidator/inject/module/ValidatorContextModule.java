package org.metadatacenter.spreadsheetvalidator.inject.module;

import dagger.Module;
import dagger.Provides;
import org.metadatacenter.spreadsheetvalidator.GeneralConfig;
import org.metadatacenter.spreadsheetvalidator.RepairClosures;
import org.metadatacenter.spreadsheetvalidator.ValidationResultAccumulatorProvider;
import org.metadatacenter.spreadsheetvalidator.ValidationSettings;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = {
    WebResourceModule.class,
})
public class ValidatorContextModule {

  @Provides
  public RepairClosures providesRepairClosures() {
    return new RepairClosures();
  }

  @Provides
  public ValidationResultAccumulatorProvider providesValidationResultCollectorProvider() {
    return new ValidationResultAccumulatorProvider();
  }

  @Provides
  public ValidationSettings providesValidationSettings(GeneralConfig generalConfig) {
    return new ValidationSettings(generalConfig);
  }
}
