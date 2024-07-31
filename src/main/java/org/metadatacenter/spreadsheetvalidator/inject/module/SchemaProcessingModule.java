package org.metadatacenter.spreadsheetvalidator.inject.module;

import com.fasterxml.jackson.databind.node.ObjectNode;
import dagger.Module;
import dagger.Provides;
import org.metadatacenter.artifacts.model.reader.ArtifactReader;
import org.metadatacenter.spreadsheetvalidator.CedarSpreadsheetSchemaParser;
import org.metadatacenter.spreadsheetvalidator.excel.ExcelSpreadsheetSchemaParser;
import org.metadatacenter.spreadsheetvalidator.excel.model.BuiltinTypeMap;
import org.metadatacenter.spreadsheetvalidator.excel.model.RequirementLevelMap;
import org.metadatacenter.spreadsheetvalidator.thirdparty.TerminologyService;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = {
    ArtifactReaderModule.class,
    TerminologyServiceModule.class,
    BuiltinTypeModule.class,
    RequirementLevelModule.class
})
public class SchemaProcessingModule {

  @Provides
  public CedarSpreadsheetSchemaParser provideCedarSpreadsheetSchemaParser(ArtifactReader<ObjectNode> artifactReader,
                                                                          TerminologyService terminologyService) {
    return new CedarSpreadsheetSchemaParser(artifactReader, terminologyService);
  }

  @Provides
  public ExcelSpreadsheetSchemaParser provideExcelSpreadsheetSchemaParser(BuiltinTypeMap builtinTypeMap,
                                                                          RequirementLevelMap requirementLevelMap) {
    return new ExcelSpreadsheetSchemaParser(builtinTypeMap, requirementLevelMap);
  }
}
