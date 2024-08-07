package org.metadatacenter.spreadsheetvalidator.inject.module;

import dagger.Module;
import dagger.Provides;
import org.metadatacenter.spreadsheetvalidator.excel.DataTableVisitor;
import org.metadatacenter.spreadsheetvalidator.excel.ExcelReader;
import org.metadatacenter.spreadsheetvalidator.excel.PropertiesTableVisitor;
import org.metadatacenter.spreadsheetvalidator.excel.ProvenanceTableVisitor;
import org.metadatacenter.spreadsheetvalidator.excel.SchemaTableVisitor;
import org.metadatacenter.spreadsheetvalidator.excel.model.PropertiesKeyword;
import org.metadatacenter.spreadsheetvalidator.excel.model.ProvenanceKeyword;
import org.metadatacenter.spreadsheetvalidator.excel.model.SchemaKeyword;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes =
  ReservedKeywordModule.class
)
public class ExcelFileProcessorModule {

  @Provides
  @Singleton
  public ExcelReader provideExcelReader() {
    return new ExcelReader();
  }

  @Provides
  public PropertiesTableVisitor propertiesTableVisitor(@Nonnull ExcelReader excelReader,
                                                       @Nonnull PropertiesKeyword propertiesKeyword) {
    return new PropertiesTableVisitor(excelReader, propertiesKeyword);
  }

  @Provides
  public DataTableVisitor provideDataTableVisitor(@Nonnull ExcelReader excelReader) {
    return new DataTableVisitor(excelReader);
  }

  @Provides
  public SchemaTableVisitor provideSchemaTableVisitor(@Nonnull ExcelReader excelReader,
                                                      @Nonnull SchemaKeyword schemaKeyword) {
    return new SchemaTableVisitor(excelReader, schemaKeyword);
  }

  @Provides
  public ProvenanceTableVisitor provenanceTableVisitor(@Nonnull ExcelReader excelReader,
                                                       @Nonnull ProvenanceKeyword provenanceKeyword) {
    return new ProvenanceTableVisitor(excelReader, provenanceKeyword);
  }
}
