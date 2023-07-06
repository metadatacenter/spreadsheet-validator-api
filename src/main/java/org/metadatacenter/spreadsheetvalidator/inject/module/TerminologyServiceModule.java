package org.metadatacenter.spreadsheetvalidator.inject.module;

import dagger.Module;
import dagger.Provides;
import org.metadatacenter.spreadsheetvalidator.thirdparty.CedarConfig;
import org.metadatacenter.spreadsheetvalidator.thirdparty.RestServiceHandler;
import org.metadatacenter.spreadsheetvalidator.thirdparty.TerminologyService;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = {
    WebResourceModule.class,
    RestServiceModule.class
})
public class TerminologyServiceModule {

  @Provides
  public TerminologyService getTerminologyService(CedarConfig cedarConfig,
                                            RestServiceHandler restServiceHandler) {
    return new TerminologyService(cedarConfig, restServiceHandler);
  }
}
