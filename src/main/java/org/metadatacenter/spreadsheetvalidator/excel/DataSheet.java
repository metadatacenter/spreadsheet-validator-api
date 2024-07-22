package org.metadatacenter.spreadsheetvalidator.excel;

import com.google.auto.value.AutoValue;
import org.apache.poi.ss.usermodel.Sheet;
import org.metadatacenter.spreadsheetvalidator.request.BadFileException;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class DataSheet {

  public enum Configuration {
    WITH_SCHEMA, NO_SCHEMA
  }

  protected static DataSheet create(@Nonnull Sheet excelSheet,
                                    @Nonnull Configuration tableConfiguration,
                                    @Nonnull ExcelDataExtractor dataExtractor) {
    return new AutoValue_DataSheet(excelSheet, tableConfiguration, dataExtractor);
  }

  @Nonnull
  public abstract Sheet getExcelSheet();

  @Nonnull
  public abstract Configuration getTableConfiguration();

  @Nonnull
  public abstract ExcelDataExtractor getDataExtractor();

  public boolean isSchemaIncluded() {
    return getTableConfiguration() == Configuration.WITH_SCHEMA;
  }

  @Nonnull
  public Optional<DataSchemaTable> getDataSchemaTable() {
    if (!isSchemaIncluded()) {
      return Optional.empty();
    }
    var separatorIndex = getDataExtractor().findFirstEmptyRowIndex(getExcelSheet());
    if (separatorIndex == -1) {
      throw new BadFileException("Bad Excel file", new MissingSeparatorRowException());
    }
    // Extract the schema section from the data sheet.
    var startSchemaRowIndex = getExcelSheet().getTopRow();
    var endSchemaRowIndex = separatorIndex - 1;
    var schemaRows = getDataExtractor().getRows(getExcelSheet(), startSchemaRowIndex, endSchemaRowIndex);
    // Extract the table header section from the data sheet.
    var headerRowIndex = separatorIndex + 1;
    var headerRow = getDataExtractor().getRow(getExcelSheet(), headerRowIndex);

    return Optional.of(DataSchemaTable.create(headerRow, schemaRows, getDataExtractor()));
  }

  @Nonnull
  public DataSchemaTable getUncheckedSchemaTable() {
    return getDataSchemaTable().get();
  }

  @Nonnull
  public DataRecordTable getDataRecordTable() {
    // Determine the row indexes to extract the table header and table data.
    var separatorIndex = getDataExtractor().findFirstEmptyRowIndex(getExcelSheet());
    var headerRowIndex = (separatorIndex != -1)
        ? separatorIndex + 1            // The header row is after the separator row
        : getExcelSheet().getTopRow();  // The header row is always at the top
    var startDataRowIndex = headerRowIndex + 1;
    var endDataRowIndex = getExcelSheet().getLastRowNum();
    // Extract the table header and table data.
    var headerRow = getDataExtractor().getRow(getExcelSheet(), headerRowIndex);
    var dataRows = getDataExtractor().getRows(getExcelSheet(), startDataRowIndex, endDataRowIndex);
    return DataRecordTable.create(headerRow, dataRows, getDataExtractor());
  }
}
