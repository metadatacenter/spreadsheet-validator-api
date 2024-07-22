package org.metadatacenter.spreadsheetvalidator.excel;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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
public abstract class DataSchemaTable {

  public static DataSchemaTable create(@Nonnull Row headerRow,
                                       @Nonnull List<Row> schemaRows,
                                       @Nonnull ExcelDataExtractor dataExtractor) {
    return new AutoValue_DataSchemaTable(headerRow, schemaRows, dataExtractor);
  }

  @Nonnull
  public abstract Row getHeaderRow();

  @Nonnull
  public abstract List<Row> getSchemaRows();

  @Nonnull
  public abstract ExcelDataExtractor getDataExtractor();

  public int columnLength() {
    return getSchemaRows().get(0).getPhysicalNumberOfCells();
  }

  public List<Map<String, Object>> asMaps() {
    // Use the first row as a sample to count the number of cells.
    return IntStream.range(1, columnLength()) // skip the first row
        .mapToObj(this::getColumn)
        .collect(ImmutableList.toImmutableList());
  }

  public Map<String, Object> getColumn(int columnIndex) {
    return getSchemaRows().stream()
        .collect(ImmutableMap.toImmutableMap(
            this::getKey,
            row -> getValue(row, columnIndex)
        ));
  }

  private String getKey(Row row) {
    return getDataExtractor().getStringValue(row, 0);
  }

  private Object getValue(Row row, int columnIndex) {
    return getDataExtractor().getValue(row, columnIndex);
  }
}
