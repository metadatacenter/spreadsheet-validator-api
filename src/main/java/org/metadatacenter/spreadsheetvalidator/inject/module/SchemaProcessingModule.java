package org.metadatacenter.spreadsheetvalidator.inject.module;

import dagger.Module;
import dagger.Provides;
import org.metadatacenter.artifacts.model.reader.ArtifactReader;
import org.metadatacenter.spreadsheetvalidator.SpreadsheetSchemaGenerator;
import org.metadatacenter.spreadsheetvalidator.thirdparty.TerminologyService;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = {
    ArtifactReaderModule.class,
    TerminologyServiceModule.class
})
public class SchemaProcessingModule {

  @Provides
  public SpreadsheetSchemaGenerator getSpreadsheetSchemaGenerator(ArtifactReader artifactReader,
                                                                  TerminologyService terminologyService) {
    return new SpreadsheetSchemaGenerator(artifactReader, terminologyService);
  }
}
