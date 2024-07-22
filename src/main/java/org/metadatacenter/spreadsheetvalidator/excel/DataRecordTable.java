package org.metadatacenter.spreadsheetvalidator.excel;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class DataRecordTable {

  public static DataRecordTable create(@Nonnull Row headerRow,
                                       @Nonnull List<Row> recordRows,
                                       @Nonnull ExcelDataExtractor dataExtractor) {
    return new AutoValue_DataRecordTable(headerRow, recordRows, dataExtractor);
  }

  @Nonnull
  public abstract Row getHeaderRow();

  @Nonnull
  public abstract List<Row> getRecordRows();

  @Nonnull
  public abstract ExcelDataExtractor getDataExtractor();

  public List<String> getHeaderColumnNames() {
    return Streams.stream(getHeaderRow().cellIterator())
        .map(Cell::getStringCellValue)
        .collect(ImmutableList.toImmutableList());
  }

  public List<Map<String, Object>> asMaps(Map<String, String> columnMapping) {
    return IntStream.range(0, getRecordRows().size())
        .mapToObj(rowIndex -> createRecordObject(rowIndex, columnMapping))
        .collect(ImmutableList.toImmutableList());
  }

  private ImmutableMap<String, Object> createRecordObject(int rowIndex, Map<String, String> columnMapping) {
    var record = getRecordRows().get(rowIndex);
    return getDataExtractor().toStream(getHeaderRow())
        .collect(ImmutableMap.toImmutableMap(
            header -> getKey(header, columnMapping),
            header -> getValue(header, record)));
  }

  private String getKey(Cell headerCell, Map<String, String> columnMapping) {
    var columnLabel = headerCell.getStringCellValue();
    var columnName = columnMapping.getOrDefault(columnLabel, columnLabel);
    return columnName;
  }

  private Object getValue(Cell header, Row record) {
    var columnIndex = header.getColumnIndex();
    return getDataExtractor().getValue(record, columnIndex);
  }
}
