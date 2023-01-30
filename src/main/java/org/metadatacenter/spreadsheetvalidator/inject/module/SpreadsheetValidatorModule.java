package org.metadatacenter.spreadsheetvalidator.inject.module;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import org.metadatacenter.spreadsheetvalidator.ResultCollector;
import org.metadatacenter.spreadsheetvalidator.SpreadsheetValidator;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.inject.provider.SpreadsheetValidatorProvider;
import org.metadatacenter.spreadsheetvalidator.validator.ResultCollectorImpl;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = ValidatorContextModule.class)
public class SpreadsheetValidatorModule {

  @Provides
  public SpreadsheetValidator provideSpreadsheetValidator(@Nonnull ValidatorContext validatorContext) {
    return new SpreadsheetValidatorProvider(validatorContext).get();
  }

  @Provides
  public ResultCollector provideResultCollector() {
    return new ResultCollectorImpl();
  }
}
