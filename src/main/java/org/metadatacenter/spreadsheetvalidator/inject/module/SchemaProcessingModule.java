package org.metadatacenter.spreadsheetvalidator.inject.module;

import dagger.Module;
import dagger.Provides;
import org.metadatacenter.artifacts.model.reader.ArtifactReader;
import org.metadatacenter.spreadsheetvalidator.SpreadsheetSchemaGenerator;
import org.metadatacenter.spreadsheetvalidator.excel.HeaderBasedSchemaExtractor;
import org.metadatacenter.spreadsheetvalidator.excel.model.BuiltinTypeMap;
import org.metadatacenter.spreadsheetvalidator.excel.model.RequirementLevelMap;
import org.metadatacenter.spreadsheetvalidator.excel.model.ReservedKeyword;
import org.metadatacenter.spreadsheetvalidator.thirdparty.TerminologyService;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = {
    ArtifactReaderModule.class,
    TerminologyServiceModule.class,s
    DefaultHeaderBasedSchemaParser.class
})
public class SchemaProcessingModule {

  @Provides
  public SpreadsheetSchemaGenerator getSpreadsheetSchemaGenerator(ArtifactReader artifactReader,
                                                                  TerminologyService terminologyService) {
    return new SpreadsheetSchemaGenerator(artifactReader, terminologyService);
  }

  @Provides
  public HeaderBasedSchemaExtractor getHeaderBasedSchemaExtractor(ReservedKeyword reservedKeyword,
                                                                  BuiltinTypeMap builtinTypeMap,
                                                                  RequirementLevelMap requirementLevelMap) {
    return new HeaderBasedSchemaExtractor(reservedKeyword, builtinTypeMap, requirementLevelMap);
  }
}
