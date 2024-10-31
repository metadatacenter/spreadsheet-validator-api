package org.metadatacenter.spreadsheetvalidator.excel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.apache.poi.ss.usermodel.CellType.BLANK;
import static org.apache.poi.ss.usermodel.CellType.STRING;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ExcelReader {

  public Row getHeaderRow(Sheet sheet) {
    return getRow(sheet, sheet.getTopRow());
  }

  public List<Row> getRows(Sheet sheet, int fromRow, int toRow) {
    return IntStream.rangeClosed(fromRow, toRow)
        .mapToObj(index -> getRow(sheet, index))
        .collect(ImmutableList.toImmutableList());
  }

  public Row getRow(Sheet sheet, int rowIndex) {
    return sheet.getRow(rowIndex);
  }

  public Stream<Cell> toStream(Row row) {
    return Streams.stream(row.cellIterator());
  }

  public Optional<Row> findFirstEmptyRow(Sheet sheet) {
    for (int i = sheet.getFirstRowNum(); i < sheet.getLastRowNum(); i++) {
      var row = sheet.getRow(i);
      if (row == null || isRowEmpty(row)) {
        return Optional.of(sheet.createRow(i));
      }
    }
    return Optional.empty();
  }

  public Optional<List<Row>> findFirstXEmptyRows(Sheet sheet, int numEmptyRows) {
    var emptyRows = Lists.<Row>newArrayList();
    for (int i = sheet.getFirstRowNum(); i < sheet.getLastRowNum(); i++) {
      var row = sheet.getRow(i);
      if (row == null || isRowEmpty(row)) {
        emptyRows.add(sheet.createRow(i));
        if (emptyRows.size() == numEmptyRows) {
          break;
        }
      }
    }
    return emptyRows.isEmpty() ? Optional.empty() : Optional.of(ImmutableList.copyOf(emptyRows));
  }

  private boolean isRowEmpty(Row row) {
    for (Cell cell : row) {
      if (cell.getCellType() != BLANK && cell.getCellType() != STRING
          || (cell.getCellType() == STRING && !cell.getStringCellValue().trim().isEmpty())) {
        return false;
      }
    }
    return true;
  }

  public int findColumnIndex(Row row, String value) {
    return IntStream.range(0, row.getPhysicalNumberOfCells())
        .filter(i -> value.equals(row.getCell(i).getStringCellValue()))
        .findFirst()
        .orElse(-1);
  }

  @Nullable
  public Object getValue(Sheet sheet, int rowIndex, int columnIndex) {
    var row = sheet.getRow(rowIndex);
    return getValue(row, columnIndex);
  }

  @Nullable
  public Object getValue(Row row, int columnIndex) {
    var cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
    if (cell == null) {
      return null;
    }
    var cellType = cell.getCellType();
    return switch (cellType) {
      case STRING -> getStringCellValue(cell);
      case NUMERIC -> getNumericCellValue(cell);
      case BOOLEAN -> getBooleanCellValue(cell);
      case FORMULA -> getFormulaCellValue(cell);
      default -> null;
    };
  }

  private static Number parseNumber(double numericValue) {
    if (numericValue % 1 == 0) {
      return (int) numericValue;
    } else {
      return numericValue;
    }
  }

  public String getStringValue(Sheet sheet, int rowIndex, int columnIndex) {
    return getStringValue(sheet.getRow(rowIndex), columnIndex);
  }

  public String getStringValue(Row row, int columnIndex) {
    return getStringCellValue(row.getCell(columnIndex));
  }

  private String getStringCellValue(Cell cell) {
    return cell.getStringCellValue();
  }

  public Number getNumberValue(Sheet sheet, int rowIndex, int columnIndex) {
    return getNumberValue(sheet.getRow(rowIndex), columnIndex);
  }

  public Number getNumberValue(Row row, int columnIndex) {
    return getNumericCellValue(row.getCell(columnIndex));
  }

  private Number getNumericCellValue(Cell cell) {
    var numericValue = cell.getNumericCellValue();
    return parseNumber(numericValue);
  }

  public boolean getBooleanValue(Sheet sheet, int rowIndex, int columnIndex) {
    return getBooleanValue(sheet.getRow(rowIndex), columnIndex);
  }

  public boolean getBooleanValue(Row row, int columnIndex) {
    return getBooleanCellValue(row.getCell(columnIndex));
  }

  private boolean getBooleanCellValue(Cell cell) {
    return cell.getBooleanCellValue();
  }

  @Nullable
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
