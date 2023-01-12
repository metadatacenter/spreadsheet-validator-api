package org.metadatacenter.spreadsheetvalidator.inject.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import org.metadatacenter.spreadsheetvalidator.inject.provider.ObjectMapperProvider;
import org.metadatacenter.spreadsheetvalidator.inject.scope.ServiceSessionScope;

import javax.inject.Singleton;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class ObjectMapperModule {

  @Provides
  @Singleton
  ObjectMapper provideObjectMapper() {
    return new ObjectMapperProvider().get();
  }
}
