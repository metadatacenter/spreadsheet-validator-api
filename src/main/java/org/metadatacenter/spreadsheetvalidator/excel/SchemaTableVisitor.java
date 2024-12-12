package org.metadatacenter.spreadsheetvalidator.excel;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.Row;
import org.metadatacenter.spreadsheetvalidator.excel.model.ExcelSheetVisitor;
import org.metadatacenter.spreadsheetvalidator.excel.model.SchemaKeyword;
import org.metadatacenter.spreadsheetvalidator.excel.model.SchemaTable;
import org.metadatacenter.spreadsheetvalidator.request.BadFileException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collections;
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

    // Find the separator rows from the sheet
    var separatorRows = excelReader.findSeparatorRows(dataSheet);
    if (separatorRows.isEmpty()) {
      throw new BadFileException("Bad Excel file", new MissingSeparatorRowException());
    }

    // A schema table must be included, but its location depends on the number of separator rows.
    int startSchemaTableIndex, endSchemaTableIndex, dataHeaderIndex;

    if (separatorRows.size() == 1) {
      var separatorIndex = separatorRows.get(0).getRowNum();
      startSchemaTableIndex = 0;
      endSchemaTableIndex = separatorIndex - 1;
      dataHeaderIndex = separatorIndex + 1;
    } else {
      var topSeparatorIndex = separatorRows.get(0).getRowNum();
      var bottomSeparatorIndex = separatorRows.get(1).getRowNum();
      startSchemaTableIndex = topSeparatorIndex + 1;
      endSchemaTableIndex = bottomSeparatorIndex - 1;
      dataHeaderIndex = bottomSeparatorIndex + 1;
    }
    var schemaRows = excelReader.getRows(dataSheet, startSchemaTableIndex, endSchemaTableIndex);
    var headerRow = excelReader.getRow(dataSheet, dataHeaderIndex);
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
    var mutableMap = Maps.<String, Object>newHashMap();

    // Construct a schema map based on the schema rows
    schemaRows.forEach(row ->
        mutableMap.put(getKey(row), getValue(row, columnIndex))
    );
    // Add the column label info into the map based on the header row
    mutableMap.put(schemaKeyword.ofLabel(), excelReader.getStringValue(headerRow, columnIndex));

    return Collections.unmodifiableMap(mutableMap);
  }

  private String getKey(Row row) {
    return excelReader.getStringValue(row, 0);
  }

  @Nullable
  private Object getValue(Row row, int columnIndex) {
    return excelReader.getValue(row, columnIndex);
  }
}
