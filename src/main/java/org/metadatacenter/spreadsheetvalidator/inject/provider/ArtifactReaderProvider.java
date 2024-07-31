package org.metadatacenter.spreadsheetvalidator.inject.provider;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.artifacts.model.reader.ArtifactReader;
import org.metadatacenter.artifacts.model.reader.JsonSchemaArtifactReader;

import javax.inject.Provider;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ArtifactReaderProvider implements Provider<ArtifactReader<ObjectNode>> {

  @Override
  public ArtifactReader<ObjectNode> get() {
    return new JsonSchemaArtifactReader();
  }
}
