package org.metadatacenter.spreadsheetvalidator.excel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;
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

  public Stream<Cell> toStream(Row row) {
    return Streams.stream(row.cellIterator());
  }

  public int findFirstEmptyRowIndex(Sheet sheet) {
    return IntStream.rangeClosed(sheet.getFirstRowNum(), sheet.getLastRowNum())
        .filter(rowNum -> sheet.getRow(rowNum) == null)
        .findFirst()
        .orElse(-1);
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
    var cell = row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
    var cellType = cell.getCellType();
    return switch (cellType) {
      case STRING -> cell.getStringCellValue();
      case NUMERIC -> cell.getNumericCellValue();
      case BOOLEAN -> cell.getBooleanCellValue();
      default -> "";
    };
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
