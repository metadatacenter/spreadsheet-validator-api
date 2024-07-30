package org.metadatacenter.spreadsheetvalidator.inject.module;

import dagger.Module;
import dagger.Provides;
import org.metadatacenter.spreadsheetvalidator.excel.model.BuiltinTypeMap;
import org.metadatacenter.spreadsheetvalidator.excel.model.DefaultBuiltinTypeMap;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class BuiltinTypeModule {

  @Provides
  public BuiltinTypeMap provideBuiltinTypeMap() {
    return new DefaultBuiltinTypeMap();
  }
}
