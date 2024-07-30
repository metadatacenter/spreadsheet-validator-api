package org.metadatacenter.spreadsheetvalidator.excel;

import com.google.common.collect.ImmutableMap;
import org.apache.poi.ss.usermodel.Row;
import org.metadatacenter.spreadsheetvalidator.excel.model.ExcelSheetVisitor;
import org.metadatacenter.spreadsheetvalidator.excel.model.SchemaKeyword;
import org.metadatacenter.spreadsheetvalidator.excel.model.SchemaTable;
import org.metadatacenter.spreadsheetvalidator.request.BadFileException;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class SchemaTableVisitor implements ExcelSheetVisitor<SchemaTable> {

  private final ExcelReader excelReader;

  private final SchemaKeyword schemaKeyword;

  private static final String DEFAULT_TABLE_SHEET_NAME = "MAIN";

  @Inject
  public SchemaTableVisitor(@Nonnull ExcelReader excelReader,
                            @Nonnull SchemaKeyword schemaKeyword) {
    this.excelReader = checkNotNull(excelReader);
    this.schemaKeyword = checkNotNull(schemaKeyword);
  }

  @Override
  public SchemaTable visit(ExcelWorkbook workbook) {
    var dataSheet = workbook.getSheet(DEFAULT_TABLE_SHEET_NAME).orElse(workbook.getFirstSheet());

    // Determine the row indexes to extract the table header and table data.
    var separatorRow = excelReader.findFirstEmptyRow(dataSheet);
    if (separatorRow.isEmpty()) {
      throw new BadFileException("Bad Excel file", new MissingSeparatorRowException());
    }
    var separatorIndex = separatorRow.get().getRowNum();

    // Extract the schema section from the data sheet.
    var startSchemaRowIndex = dataSheet.getTopRow();
    var endSchemaRowIndex = separatorIndex - 1;
    var schemaRows = excelReader.getRows(dataSheet, startSchemaRowIndex, endSchemaRowIndex);

    // Extract the table header section from the data sheet.
    var headerRowIndex = separatorIndex + 1;
    var headerRow = excelReader.getRow(dataSheet, headerRowIndex);

    var records = processRow(schemaRows, headerRow);
    return SchemaTable.create(records, schemaKeyword);
  }

  private Map<String, Map<String, Object>> processRow(List<Row> schemaRows, Row headerRow) {
    // Use the first row as a sample to count the number of cells.
    var numberOfDataColumns = schemaRows.get(0).getPhysicalNumberOfCells();

    // Collect data into a LinkedHashMap
    var orderedMap = IntStream.range(1, numberOfDataColumns) // skip the first row
        .boxed()
        .collect(Collectors.toMap(
            columnIndex -> excelReader.getStringValue(headerRow, columnIndex),
            columnIndex -> getDataSchema(schemaRows, headerRow, columnIndex),
            (existing, replacement) -> existing,
            LinkedHashMap::new
        ));
    return ImmutableMap.copyOf(orderedMap);
  }

  public Map<String, Object> getDataSchema(List<Row> schemaRows, Row headerRow, int columnIndex) {
    var builder = ImmutableMap.<String, Object>builder();
    // Construct a schema map based on the schema rows
    schemaRows.forEach(row ->
        builder.put(getKey(row), getValue(row, columnIndex))
    );
    // Add the column label info into the map based on the header row
    builder.put(schemaKeyword.ofLabel(), excelReader.getStringValue(headerRow, columnIndex));

    return builder.build();
  }

  private String getKey(Row row) {
    return excelReader.getStringValue(row, 0);
  }

  private Object getValue(Row row, int columnIndex) {
    return excelReader.getValue(row, columnIndex);
  }
}
