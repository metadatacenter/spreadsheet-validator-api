package org.metadatacenter.spreadsheetvalidator.excel.model;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class DataTable {

  @Nonnull
  public static DataTable create(@Nonnull List<Map<String, Object>> records) {
    return new AutoValue_DataTable(records);
  }

  @Nonnull
  public abstract List<Map<String, Object>> getRecords();

  @Nonnull
  public Map<String, Object> getRecord(int index) {
    return getRecords().get(index);
  }

  @Nonnull
  public Optional<Object> getValue(int recordIndex, String columnName) {
    return Optional.ofNullable(getRecord(recordIndex).get(columnName));
  }
}
