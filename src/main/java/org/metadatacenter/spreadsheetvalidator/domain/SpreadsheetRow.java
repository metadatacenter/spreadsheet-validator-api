package org.metadatacenter.spreadsheetvalidator.domain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class SpreadsheetRow {

  private static final String ROW_NUM = "rowNum";

  @JsonCreator
  public static SpreadsheetRow create(@JsonProperty(ROW_NUM) int rowNumber,
                                      @Nonnull Map<String, Object> object) {
    return new AutoValue_SpreadsheetRow(rowNumber, object);
  }

  @JsonProperty(ROW_NUM)
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
}
