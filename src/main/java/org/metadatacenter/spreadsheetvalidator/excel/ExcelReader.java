package org.metadatacenter.spreadsheetvalidator.excel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.apache.poi.ss.usermodel.CellType.BLANK;
import static org.apache.poi.ss.usermodel.CellType.STRING;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ExcelReader {

  public Row getHeaderRow(CachedSheet sheet) {
    return getRow(sheet, sheet.getTopRow());
  }

  public List<Row> getRows(CachedSheet sheet, int fromRow, int toRow) {
    return IntStream.rangeClosed(fromRow, toRow)
        .mapToObj(index -> getRow(sheet, index))
        .collect(ImmutableList.toImmutableList());
  }

  public Row getRow(CachedSheet sheet, int rowIndex) {
    return sheet.getRow(rowIndex);
  }

  public Stream<Cell> toStream(Row row) {
    return Streams.stream(row.cellIterator());
  }

  public ImmutableList<Integer> findSeparatorRows(CachedSheet sheet) {
    var separatorRows = new java.util.ArrayList<Integer>();
    var lastRowNum = sheet.getLastRowNum();

    for (int rowNum = sheet.getFirstRowNum(); rowNum < lastRowNum; rowNum++) {
      var currentRow = sheet.getRow(rowNum);
      if (currentRow == null || isRowEmpty(currentRow)) {
        var nextRow = sheet.getRow(rowNum + 1);
        // Check if the current and next rows are both empty, then break
        if (nextRow == null || isRowEmpty(nextRow)) {
          break;
        } else {
          // Add the row index if it's empty and the next row is not empty
          separatorRows.add(rowNum);
        }
      }
    }
    return ImmutableList.copyOf(separatorRows);
  }

  private boolean isRowEmpty(Row row) {
    for (Cell cell : row) {
      if (cell.getCellType() != BLANK
          && cell.getCellType() != STRING
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
  public Object getValue(CachedSheet sheet, int rowIndex, int columnIndex) {
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

  public String getStringValue(CachedSheet sheet, int rowIndex, int columnIndex) {
    return getStringValue(sheet.getRow(rowIndex), columnIndex);
  }

  public String getStringValue(Row row, int columnIndex) {
    return getStringCellValue(row.getCell(columnIndex));
  }

  private String getStringCellValue(Cell cell) {
    return cell.getStringCellValue();
  }

  public Number getNumberValue(CachedSheet sheet, int rowIndex, int columnIndex) {
    return getNumberValue(sheet.getRow(rowIndex), columnIndex);
  }

  public Number getNumberValue(Row row, int columnIndex) {
    return getNumericCellValue(row.getCell(columnIndex));
  }

  private Number getNumericCellValue(Cell cell) {
    var numericValue = cell.getNumericCellValue();
    return parseNumber(numericValue);
  }

  public boolean getBooleanValue(CachedSheet sheet, int rowIndex, int columnIndex) {
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
    var cachedType = cell.getCachedFormulaResultType();
    return switch (cachedType) {
      case STRING -> getStringCellValue(cell);
      case NUMERIC -> getNumericCellValue(cell);
      case BOOLEAN -> getBooleanCellValue(cell);
      default -> null;
    };
  }
}
