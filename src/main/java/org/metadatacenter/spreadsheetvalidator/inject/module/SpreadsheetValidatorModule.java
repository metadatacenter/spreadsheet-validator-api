package org.metadatacenter.spreadsheetvalidator.inject.module;

import dagger.Module;
import dagger.Provides;
import org.metadatacenter.spreadsheetvalidator.RepairClosures;
import org.metadatacenter.spreadsheetvalidator.ResultCollector;
import org.metadatacenter.spreadsheetvalidator.SpreadsheetValidator;
import org.metadatacenter.spreadsheetvalidator.ValidationResultAccumulatorProvider;
import org.metadatacenter.spreadsheetvalidator.ValidationSettings;
import org.metadatacenter.spreadsheetvalidator.inject.provider.SpreadsheetValidatorProvider;
import org.metadatacenter.spreadsheetvalidator.thirdparty.ChatGptService;
import org.metadatacenter.spreadsheetvalidator.validator.ResultCollectorImpl;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = {
    ValidatorContextModule.class,
    ChatGptServiceModule.class
})
public class SpreadsheetValidatorModule {

  @Provides
  public SpreadsheetValidator provideSpreadsheetValidator(@Nonnull RepairClosures repairClosures,
                                                          @Nonnull ValidationResultAccumulatorProvider validationResultAccumulatorProvider,
                                                          @Nonnull ValidationSettings validationSettings,
                                                          @Nonnull ChatGptService chatGptService) {
    return new SpreadsheetValidatorProvider(repairClosures, validationResultAccumulatorProvider, validationSettings, chatGptService).get();
  }

  @Provides
  public ResultCollector provideResultCollector() {
    return new ResultCollectorImpl();
  }
}
