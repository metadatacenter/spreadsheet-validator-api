package org.metadatacenter.spreadsheetvalidator.inject.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import org.metadatacenter.artifacts.model.reader.ArtifactReader;
import org.metadatacenter.spreadsheetvalidator.inject.provider.ArtifactReaderProvider;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = ObjectMapperModule.class)
public class ArtifactReaderModule {

  @Provides
  public ArtifactReader provideArtifactReader(ObjectMapper objectMapper) {
    return new ArtifactReaderProvider(objectMapper).get();
  }
}
