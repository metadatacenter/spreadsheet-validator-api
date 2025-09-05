package org.metadatacenter.spreadsheetvalidator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import org.apache.poi.ss.usermodel.Cell;
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
      if (data == null) {
        continue; // Skip empty rows
      }
      var map = headers.stream()
          .collect(toMap(
              header -> header,
              header -> {
                var column = headers.indexOf(header);
                var cell = data.getCell(column);
                if (cell == null) {
                  return ""; // Return empty string for missing cells
                }
                return getValue(cell);
              }));
      content.add(map);
    }
    return ImmutableList.copyOf(content);
  }

  public String getStringCellValue(XSSFSheet sheet, int rowIndex, int columnIndex) {
    return sheet.getRow(rowIndex).getCell(columnIndex).getStringCellValue();
  }

  private Object getValue(Cell cell) {
    var cellType = cell.getCellType();
    return switch (cellType) {
      case STRING -> getStringCellValue(cell);
      case NUMERIC -> getNumericCellValue(cell);
      case BOOLEAN -> getBooleanCellValue(cell);
      case FORMULA -> getFormulaCellValue(cell);
      default -> "";
    };
  }

  private String getStringCellValue(Cell cell) {
    return cell.getStringCellValue();
  }

  private Number getNumericCellValue(Cell cell) {
    var numericValue = cell.getNumericCellValue();
    return parseNumber(numericValue);
  }

  private static Number parseNumber(double numericValue) {
    if (numericValue % 1 == 0) {
      return (int) numericValue;
    } else {
      return numericValue;
    }
  }

  private boolean getBooleanCellValue(Cell cell) {
    return cell.getBooleanCellValue();
  }

  private Object getFormulaCellValue(Cell cell) {
    var workbook = cell.getSheet().getWorkbook();
    var evaluator = workbook.getCreationHelper().createFormulaEvaluator();
    var formulaResultType = evaluator.evaluateFormulaCell(cell);
    return switch (formulaResultType) {
      case STRING -> getStringCellValue(cell);
      case NUMERIC -> getNumericCellValue(cell);
      case BOOLEAN -> getBooleanCellValue(cell);
      default -> null;
    };
  }
}
