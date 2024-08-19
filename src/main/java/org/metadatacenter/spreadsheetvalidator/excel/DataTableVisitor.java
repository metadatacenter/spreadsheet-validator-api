package org.metadatacenter.spreadsheetvalidator.excel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.Row;
import org.metadatacenter.spreadsheetvalidator.excel.model.DataTable;
import org.metadatacenter.spreadsheetvalidator.excel.model.ExcelSheetVisitor;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DataTableVisitor implements ExcelSheetVisitor<DataTable> {

  private final ExcelReader excelReader;

  private static final String DEFAULT_TABLE_SHEET_NAME = "MAIN";

  @Inject
  public DataTableVisitor(@Nonnull ExcelReader excelReader) {
    this.excelReader = checkNotNull(excelReader);
  }

  @Override
  public DataTable visit(ExcelWorkbook workbook) {
    var dataSheet = workbook.getSheet(DEFAULT_TABLE_SHEET_NAME).orElse(workbook.getFirstSheet());

    // Find the first 2 empty rows from the sheet
    var emptyRows = excelReader.findFirstXEmptyRows(dataSheet, 2);

    var dataHeaderIndex = dataSheet.getFirstRowNum();
    if (emptyRows.isPresent()) {
      var separatorRows = emptyRows.get();
      dataHeaderIndex = separatorRows.get(separatorRows.size() - 1).getRowNum() + 1;
    }
    var startDataTableIndex = dataHeaderIndex + 1;
    var endDataTableIndex = dataSheet.getLastRowNum();

    // Extract the table header and table data.
    var headerRow = excelReader.getRow(dataSheet, dataHeaderIndex);
    var dataRows = excelReader.getRows(dataSheet, startDataTableIndex, endDataTableIndex);

    var records = processRows(headerRow, dataRows);
    return DataTable.create(records);
  }

  private List<Map<String, Object>> processRows(Row headerRow, List<Row> dataRows) {
    return dataRows.stream()
        .map(dataRow -> createRecordObject(headerRow, dataRow))
        .collect(ImmutableList.toImmutableList());
  }

  private Map<String, Object> createRecordObject(Row headerRow, Row dataRow) {
    var mutableMap = Maps.<String, Object>newHashMap();
    var numberOfHeaderColumns = headerRow.getPhysicalNumberOfCells();
    for (int columnIndex = 0; columnIndex < numberOfHeaderColumns; columnIndex++) {
      var key = excelReader.getStringValue(headerRow, columnIndex);
      var value = excelReader.getValue(dataRow, columnIndex);
      mutableMap.put(key, value);
    }
    return Collections.unmodifiableMap(mutableMap);
  }
}
