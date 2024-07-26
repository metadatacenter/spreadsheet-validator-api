package org.metadatacenter.spreadsheetvalidator.inject.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import dagger.Module;
import dagger.Provides;
import org.metadatacenter.spreadsheetvalidator.thirdparty.RestServiceHandler;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = {
    ObjectMapperModule.class
})
public class RestServiceModule {

  @Provides
  public RestServiceHandler getRestServiceHandler(@Nonnull ObjectMapper objectMapper) {
    return new RestServiceHandler(objectMapper);
  }
}
