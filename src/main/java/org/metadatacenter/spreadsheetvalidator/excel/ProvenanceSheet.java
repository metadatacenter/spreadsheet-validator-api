package org.metadatacenter.spreadsheetvalidator.excel;

import com.google.auto.value.AutoValue;
import org.apache.poi.ss.usermodel.Sheet;
import org.metadatacenter.spreadsheetvalidator.request.BadFileException;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ProvenanceSheet {

  private static final String SCHEMA_NAME = "schema:title";
  private static final String SCHEMA_VERSION = "pav:version";
  private static final String SCHEMA_CREATE_DATE = "pav:createdOn";
  private static final String SCHEMA_IRI = "pav:derivedFrom";

  public static ProvenanceSheet create(@Nonnull Sheet excelSheet,
                                       @Nonnull ExcelDataExtractor dataExtractor) {
    return new AutoValue_ProvenanceSheet(excelSheet, dataExtractor);
  }

  @Nonnull
  public abstract Sheet getExcelSheet();

  @Nonnull
  public abstract ExcelDataExtractor getDataExtractor();

  public String getSchemaName() {
    var columnIndex = getColumnIndex(SCHEMA_NAME);
    return getDataExtractor().getStringValue(getExcelSheet(), 1, columnIndex);
  }

  public String getSchemaVersion() {
    var columnIndex = getColumnIndex(SCHEMA_VERSION);
    return getDataExtractor().getStringValue(getExcelSheet(), 1, columnIndex);
  }

  public String getSchemaCreateDate() {
    var columnIndex = getColumnIndex(SCHEMA_CREATE_DATE);
    return getDataExtractor().getStringValue(getExcelSheet(), 1, columnIndex);
  }

  public String getSchemaIri() {
    var columnIndex = getColumnIndex(SCHEMA_IRI);
    return getDataExtractor().getStringValue(getExcelSheet(), 1, columnIndex);
  }

  private int getColumnIndex(String columnName) {
    var headerRow = getDataExtractor().getHeaderRow(getExcelSheet());
    var columnIndex = getDataExtractor().findColumnIndex(headerRow, columnName);
    if (columnIndex == -1) {
      throw new BadFileException("Bad Excel file.", new MissingProvenanceInfoException(columnName));
    }
    return columnIndex;
  }
}
