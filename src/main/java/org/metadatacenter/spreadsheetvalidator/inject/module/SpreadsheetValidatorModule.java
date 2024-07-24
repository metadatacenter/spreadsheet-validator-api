package org.metadatacenter.spreadsheetvalidator.inject.module;

import dagger.Module;
import dagger.Provides;
import org.metadatacenter.spreadsheetvalidator.RepairClosures;
import org.metadatacenter.spreadsheetvalidator.SpreadsheetValidator;
import org.metadatacenter.spreadsheetvalidator.ValidationReportHandler;
import org.metadatacenter.spreadsheetvalidator.ValidationSettings;
import org.metadatacenter.spreadsheetvalidator.inject.provider.SpreadsheetValidatorProvider;
import org.metadatacenter.spreadsheetvalidator.thirdparty.ChatGptService;
import org.metadatacenter.spreadsheetvalidator.validator.DefaultValidationReportHandler;

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
                                                          @Nonnull ValidationSettings validationSettings,
                                                          @Nonnull ChatGptService chatGptService) {
    return new SpreadsheetValidatorProvider(repairClosures, validationSettings, chatGptService).get();
  }

  @Provides
  public ValidationReportHandler provideValidationReportHandler() {
    return new DefaultValidationReportHandler();
  }
}
