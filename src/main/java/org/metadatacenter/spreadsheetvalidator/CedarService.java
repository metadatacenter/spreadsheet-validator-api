package org.metadatacenter.spreadsheetvalidator;

import org.metadatacenter.artifacts.model.reader.ArtifactReader;

import javax.inject.Inject;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CedarService {

  private final CedarConfig cedarConfig;

  private final ArtifactReader artifactReader;

  @Inject
  public CedarService(CedarConfig cedarConfig,
                      ArtifactReader artifactReader) {
    this.cedarConfig = cedarConfig;
    this.artifactReader = artifactReader;
  }
}
