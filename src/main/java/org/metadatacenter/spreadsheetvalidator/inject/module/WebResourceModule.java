package org.metadatacenter.spreadsheetvalidator.inject.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import org.jvnet.hk2.annotations.Service;
import org.metadatacenter.spreadsheetvalidator.CedarConfig;
import org.metadatacenter.spreadsheetvalidator.SpreadsheetValidatorConfiguration;
import org.metadatacenter.spreadsheetvalidator.inject.provider.ObjectMapperProvider;
import org.metadatacenter.spreadsheetvalidator.inject.scope.ServiceSessionScope;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class WebResourceModule {

  private final SpreadsheetValidatorConfiguration appConfiguration;

  public WebResourceModule(@Nonnull SpreadsheetValidatorConfiguration appConfiguration) {
    this.appConfiguration = checkNotNull(appConfiguration);
  }

  @Provides
  CedarConfig provideCedarConfiguration() {
    return appConfiguration.getCedarConfig();
  }
}
