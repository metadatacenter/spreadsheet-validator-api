package org.metadatacenter.spreadsheetvalidator.inject.module;

import dagger.Module;
import dagger.Provides;
import org.metadatacenter.spreadsheetvalidator.CedarConfig;
import org.metadatacenter.spreadsheetvalidator.CedarService;
import org.metadatacenter.spreadsheetvalidator.RestServiceHandler;
import org.metadatacenter.spreadsheetvalidator.inject.scope.ServiceSessionScope;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = {
    WebResourceModule.class,
    RestServiceModule.class
})
public class CedarServiceModule {

  @Provides
  public CedarService getCedarService(CedarConfig cedarConfig,
                                      RestServiceHandler restServiceHandler) {
    return new CedarService(cedarConfig, restServiceHandler);
  }
}
