package org.metadatacenter.spreadsheetvalidator.inject.module;

import dagger.Module;
import dagger.Provides;
import org.metadatacenter.spreadsheetvalidator.CedarConfig;
import org.metadatacenter.spreadsheetvalidator.CedarService;
import org.metadatacenter.spreadsheetvalidator.RestServiceHandler;
import org.metadatacenter.spreadsheetvalidator.TerminologyService;

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
