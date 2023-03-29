package org.metadatacenter.spreadsheetvalidator.domain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class SpreadsheetRow {

  private static final String ROW_NUM = "rowNumber";

  public static SpreadsheetRow create(int rowNumber, @Nonnull Map<String, Object> map) {
    var newMap = ImmutableMap.<String, Object>builder()
        .putAll(map)
        .put(ROW_NUM, rowNumber)
        .build();
    return new AutoValue_SpreadsheetRow(rowNumber, map);
  }

  public abstract int getRowNumber();

  @Nonnull
  @JsonAnyGetter
  public abstract Map<String, Object> getObject();

  @Nonnull
  @JsonIgnore
  public Object getValue(String columnName) {
    return getObject().get(columnName);
  }

  @Nonnull
  @JsonIgnore
  public Stream<String> columnStream() {
    return getObject().keySet().stream();
  }

  @JsonIgnore
  public int size() {
    return getObject().size();
  }

  @Nullable
  @JsonIgnore
  public Object get(String columnName) {
    return getObject().get(columnName);
  }
}
