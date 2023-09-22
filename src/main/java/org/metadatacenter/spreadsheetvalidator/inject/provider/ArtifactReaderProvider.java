package org.metadatacenter.spreadsheetvalidator.inject.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.metadatacenter.artifacts.model.reader.ArtifactReader;
import org.metadatacenter.artifacts.model.reader.JsonSchemaArtifactReader;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ArtifactReaderProvider implements Provider<ArtifactReader> {

  private final ObjectMapper objectMapper;

  @Inject
  public ArtifactReaderProvider(@Nonnull ObjectMapper objectMapper) {
    this.objectMapper = checkNotNull(objectMapper);
  }

  @Override
  public ArtifactReader get() {
    return new JsonSchemaArtifactReader();
  }
}
