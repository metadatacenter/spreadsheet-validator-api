package org.metadatacenter.spreadsheetvalidator.inject.module;

import dagger.Module;
import dagger.Provides;
import org.metadatacenter.spreadsheetvalidator.thirdparty.BioPortalConfig;
import org.metadatacenter.spreadsheetvalidator.thirdparty.BioPortalService;
import org.metadatacenter.spreadsheetvalidator.thirdparty.RestServiceHandler;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = {
    WebResourceModule.class,
    RestServiceModule.class
})
public class BioPortalServiceModule {

  @Provides
  public BioPortalService getBioPortalService(BioPortalConfig bioPortalConfig,
                                              RestServiceHandler restServiceHandler) {
    return new BioPortalService(bioPortalConfig, restServiceHandler);
  }
}
