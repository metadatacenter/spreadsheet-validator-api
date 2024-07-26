package org.metadatacenter.spreadsheetvalidator.inject.module;

import dagger.Module;
import dagger.Provides;
import org.metadatacenter.spreadsheetvalidator.excel.model.BuiltinTypeMap;
import org.metadatacenter.spreadsheetvalidator.excel.model.DefaultBuiltinTypeMap;
import org.metadatacenter.spreadsheetvalidator.excel.model.DefaultRequirementLevelMap;
import org.metadatacenter.spreadsheetvalidator.excel.model.DefaultReservedKeyword;
import org.metadatacenter.spreadsheetvalidator.excel.model.RequirementLevelMap;
import org.metadatacenter.spreadsheetvalidator.excel.model.ReservedKeyword;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class DefaultHeaderBasedSchemaParser {

  @Provides
  public ReservedKeyword provideReservedKeyword() {
    return new DefaultReservedKeyword();
  }

  @Provides
  public BuiltinTypeMap provideBuiltinTypeMap() {
    return new DefaultBuiltinTypeMap();
  }

  @Provides
  public RequirementLevelMap provideRequirementLevelMap() {
    return new DefaultRequirementLevelMap();
  }
}
