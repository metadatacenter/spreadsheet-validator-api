package org.metadatacenter.spreadsheetvalidator.inject.module;

import dagger.Module;
import dagger.Provides;
import org.metadatacenter.spreadsheetvalidator.excel.model.DefaultRequirementLevelMap;
import org.metadatacenter.spreadsheetvalidator.excel.model.RequirementLevelMap;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class RequirementLevelModule {

  @Provides
  public RequirementLevelMap provideRequirementLevelMap() {
    return new DefaultRequirementLevelMap();
  }
}
