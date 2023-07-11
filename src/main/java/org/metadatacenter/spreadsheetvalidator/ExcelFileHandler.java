package org.metadatacenter.spreadsheetvalidator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ExcelFileHandler {

  public XSSFWorkbook getWorkbookFromInputStream(final InputStream is) throws IOException {
    return new XSSFWorkbook(is);
  }

  public XSSFSheet getSheet(XSSFWorkbook workbook, String sheetName) {
    return workbook.getSheet(sheetName);
  }

  public XSSFSheet getSheet(XSSFWorkbook workbook, int sheetIndex) {
    return workbook.getSheetAt(sheetIndex);
  }

  public XSSFRow getHeaderRow(XSSFSheet sheet) {
    return sheet.getRow(0);
  }

  public XSSFRow getRow(XSSFSheet sheet, int rowIndex) {
    return sheet.getRow(rowIndex);
  }

  public Optional<Integer> findColumnLocation(XSSFSheet sheet, String columnName) {
    var columnNumber = 0;
    var headerCells = getHeaderRow(sheet).iterator();
    while (headerCells.hasNext()) {
      var cell = headerCells.next();
      if ("pav:derivedFrom".equals(cell.getStringCellValue())) {
        return Optional.of(columnNumber);
      }
      columnNumber++;
    }
    return Optional.empty();
  }

  public List<String> getHeaders(XSSFSheet sheet) {
    return Streams.stream(getHeaderRow(sheet).iterator())
        .map(Cell::getStringCellValue)
        .collect(ImmutableList.toImmutableList());
  }

  public List<Map<String, Object>> getTableData(XSSFSheet sheet) {
    var headers = getHeaders(sheet);
    var content = Lists.<Map<String, Object>>newArrayList();
    var sheetSize = sheet.getLastRowNum();
    for (int row = 1; row <= sheetSize; row++) {
      var data = sheet.getRow(row);
      var map = headers.stream()
          .collect(toMap(
              header -> header,
              header -> {
                var column = headers.indexOf(header);
                var cell = data.getCell(column);
                var cellType = cell.getCellType();
                Object value = null;
                if (cellType == CellType.NUMERIC) {
                  value = cell.getNumericCellValue();
                } else {
                  value = cell.getStringCellValue();
                }
                return value;
              }));
      content.add(map);
    }
    return ImmutableList.copyOf(content);
  }

  public String getStringCellValue(XSSFSheet sheet, int rowIndex, int columnIndex) {
    return sheet.getRow(rowIndex).getCell(columnIndex).getStringCellValue();
  }
}
