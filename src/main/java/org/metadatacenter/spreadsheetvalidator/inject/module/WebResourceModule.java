package org.metadatacenter.spreadsheetvalidator.inject.module;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.web.conf.WebConfiguration;
import org.metadatacenter.spreadsheetvalidator.thirdparty.BioPortalConfig;
import org.metadatacenter.spreadsheetvalidator.thirdparty.CedarConfig;
import org.metadatacenter.spreadsheetvalidator.SpreadsheetValidatorConfiguration;

import javax.annotation.Nonnull;

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
  WebConfiguration provideWebConfiguration() {
    return appConfiguration.getWebConfiguration();
  }

  @Provides
  CedarConfig provideCedarConfiguration() {
    return appConfiguration.getCedarConfig();
  }

  @Provides
  BioPortalConfig provideBioPortalConfiguration() {
    return appConfiguration.getBioPortalConfig();
  }
}
