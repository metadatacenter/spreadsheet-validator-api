package org.metadatacenter.spreadsheetvalidator.inject.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import dagger.Module;
import dagger.Provides;
import org.metadatacenter.spreadsheetvalidator.ExcelFileHandler;
import org.metadatacenter.spreadsheetvalidator.thirdparty.RestServiceHandler;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class ExcelFileModule {

  @Provides
  public ExcelFileHandler getExcelFileHandler() {
    return new ExcelFileHandler();
  }
}
