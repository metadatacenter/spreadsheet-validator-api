package org.metadatacenter.spreadsheetvalidator.excel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ExcelDataExtractor {

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

  public Stream<Cell> getCellStream(Row row) {
    return Streams.stream(row.cellIterator());
  }

  public Optional<Row> findFirstEmptyRow(Sheet sheet) {
    return Streams.stream(sheet.rowIterator())
        .filter(this::checkIfRowIsEmpty)
        .findFirst();
  }

  private boolean checkIfRowIsEmpty(Row row) {
    if (row == null) {
      return true;
    }
    if (row.getLastCellNum() <= 0) {
      return true;
    }
    for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
      var cell = row.getCell(i);
      if (cell != null && cell.getCellType() != CellType.BLANK && StringUtils.isNotBlank(cell.toString())) {
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

  public Object getValue(Sheet sheet, int rowIndex, int columnIndex) {
    var row = sheet.getRow(rowIndex);
    return getValue(row, columnIndex);
  }

  public Object getValue(Row row, int columnIndex) {
    var cell = row.getCell(columnIndex);
    var cellType = cell.getCellType();
    switch (cellType) {
      case STRING:
        return cell.getStringCellValue();
      case NUMERIC:
        return cell.getNumericCellValue();
      case BOOLEAN:
        return cell.getBooleanCellValue();
      default:
        return "";
    }
  }

  public String getStringValue(Sheet sheet, int rowIndex, int columnIndex) {
    return getStringValue(sheet.getRow(rowIndex), columnIndex);
  }

  public String getStringValue(Row row, int columnIndex) {
    return row.getCell(columnIndex).getStringCellValue();
  }

  public double getNumberValue(Sheet sheet, int rowIndex, int columnIndex) {
    return getNumberValue(sheet.getRow(rowIndex), columnIndex);
  }

  public double getNumberValue(Row row, int columnIndex) {
    return row.getCell(columnIndex).getNumericCellValue();
  }

  public boolean getBooleanValue(Sheet sheet, int rowIndex, int columnIndex) {
    return getBooleanValue(sheet.getRow(rowIndex), columnIndex);
  }

  public boolean getBooleanValue(Row row, int columnIndex) {
    return row.getCell(columnIndex).getBooleanCellValue();
  }
}
