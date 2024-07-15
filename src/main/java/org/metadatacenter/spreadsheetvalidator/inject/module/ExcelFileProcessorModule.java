package org.metadatacenter.spreadsheetvalidator.inject.module;

import dagger.Module;
import dagger.Provides;
import org.metadatacenter.spreadsheetvalidator.excel.ExcelDataExtractor;
import org.metadatacenter.spreadsheetvalidator.excel.MetadataSpreadsheetBuilder;
import org.metadatacenter.spreadsheetvalidator.excel.MetadataSpreadsheetModelHandler;
import org.metadatacenter.spreadsheetvalidator.excel.SpreadsheetModelHandler;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class ExcelFileProcessorModule {

  @Provides
  public SpreadsheetModelHandler provideSpreadsheetModelHandler() {
    return new MetadataSpreadsheetModelHandler();
  }

  @Provides
  public MetadataSpreadsheetBuilder provideMetadataSpreadsheetBuilder(SpreadsheetModelHandler spreadsheetModelHandler) {
    return new MetadataSpreadsheetBuilder(
        spreadsheetModelHandler,
        new ExcelDataExtractor());
  }
}
