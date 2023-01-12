package org.metadatacenter.spreadsheetvalidator.inject.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import org.metadatacenter.spreadsheetvalidator.RestServiceHandler;
import org.metadatacenter.spreadsheetvalidator.inject.scope.ServiceSessionScope;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = ObjectMapperModule.class)
public class RestServiceModule {

  @Provides
  public RestServiceHandler getRestServiceHandler(ObjectMapper objectMapper) {
    return new RestServiceHandler(objectMapper);
  }
}
