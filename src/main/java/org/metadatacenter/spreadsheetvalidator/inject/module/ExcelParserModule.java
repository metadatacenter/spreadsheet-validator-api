package org.metadatacenter.spreadsheetvalidator.inject.module;

import dagger.Module;
import dagger.Provides;
import org.metadatacenter.spreadsheetvalidator.excel.ExcelParser;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class ExcelParserModule {

  @Provides
  public ExcelParser provideExcelParser() {
    return new ExcelParser();
  }
}
