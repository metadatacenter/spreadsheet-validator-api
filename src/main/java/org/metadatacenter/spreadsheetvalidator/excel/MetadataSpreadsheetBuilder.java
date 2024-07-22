package org.metadatacenter.spreadsheetvalidator.excel;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.metadatacenter.spreadsheetvalidator.request.BadFileException;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.metadatacenter.spreadsheetvalidator.excel.DataSheet.Configuration.NO_SCHEMA;
import static org.metadatacenter.spreadsheetvalidator.excel.DataSheet.Configuration.WITH_SCHEMA;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class MetadataSpreadsheetBuilder {

  private final SpreadsheetModelHandler spreadsheetModelHandler;

  private final ExcelDataExtractor dataExtractor;

  private Workbook workbook;

  private boolean isSchemaIncluded = false;

  @Inject
  public MetadataSpreadsheetBuilder(@Nonnull SpreadsheetModelHandler spreadsheetModelHandler,
                                    @Nonnull ExcelDataExtractor dataExtractor) {
    this.spreadsheetModelHandler = checkNotNull(spreadsheetModelHandler);
    this.dataExtractor = checkNotNull(dataExtractor);
  }

  @Nonnull
  public MetadataSpreadsheetBuilder openSpreadsheetFromInputStream(@Nonnull InputStream inputStream) {
    try {
      workbook = new XSSFWorkbook(inputStream);
      return this;
    } catch (IOException e) {
      throw new BadFileException("Unable to load spreadsheet", e);
    }
  }

  @Nonnull
  public MetadataSpreadsheetBuilder includeDataSchema() {
    isSchemaIncluded = true;
    return this;
  }

  @Nonnull
  public MetadataSpreadsheet build() {
    return (isSchemaIncluded)
        ? buildSpreadsheetWithEmbeddedSchema()
        : buildSpreadsheetWithProvenanceSheet();
  }

  private MetadataSpreadsheet buildSpreadsheetWithEmbeddedSchema() {
    var dataSheet = DataSheet.create(spreadsheetModelHandler.getDataSheet(workbook), WITH_SCHEMA, dataExtractor);
    return MetadataSpreadsheet.create(dataSheet);
  }

  private MetadataSpreadsheet buildSpreadsheetWithProvenanceSheet() {
    var dataSheet = DataSheet.create(spreadsheetModelHandler.getDataSheet(workbook), NO_SCHEMA, dataExtractor);
    var provenanceSheet = ProvenanceSheet.create(spreadsheetModelHandler.getProvenanceSheet(workbook), dataExtractor);
    return MetadataSpreadsheet.create(dataSheet, provenanceSheet);
  }
}
