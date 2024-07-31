package org.metadatacenter.spreadsheetvalidator.inject.module;

import com.fasterxml.jackson.databind.node.ObjectNode;
import dagger.Module;
import dagger.Provides;
import org.metadatacenter.artifacts.model.reader.ArtifactReader;
import org.metadatacenter.spreadsheetvalidator.inject.provider.ArtifactReaderProvider;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class ArtifactReaderModule {

  @Provides
  public ArtifactReader<ObjectNode> provideArtifactReader() {
    return new ArtifactReaderProvider().get();
  }
}
