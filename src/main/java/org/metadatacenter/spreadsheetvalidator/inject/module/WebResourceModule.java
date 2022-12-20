package org.metadatacenter.spreadsheetvalidator.inject.module;

import dagger.Module;
import dagger.Provides;
import org.metadatacenter.spreadsheetvalidator.CedarConfig;
import org.metadatacenter.spreadsheetvalidator.SpreadsheetValidatorConfiguration;

import javax.annotation.Nonnull;
import javax.ws.rs.ext.Provider;

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
