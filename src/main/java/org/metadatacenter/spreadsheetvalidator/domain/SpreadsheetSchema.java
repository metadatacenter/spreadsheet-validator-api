package org.metadatacenter.spreadsheetvalidator.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class SpreadsheetSchema {

  private static final String NAME = "name";

  private static final String VERSION = "version";

  private static final String COLUMN_DESCRIPTION = "columnDescription";

  private static final String COLUMN_ORDER = "columnOrder";

  private static final String GENERATED_FROM = "generatedFrom";

  private static final String ACCESS_URL = "accessUrl";

  @Nonnull
  @JsonCreator
  public static SpreadsheetSchema create(
      @Nonnull @JsonProperty(NAME) String name,
      @Nonnull @JsonProperty(VERSION) String version,
      @Nonnull @JsonProperty(COLUMN_DESCRIPTION) ImmutableMap<String, ColumnDescription> columnDescription,
      @Nonnull @JsonProperty(COLUMN_ORDER) ImmutableList<String> columnOrder,
      @Nonnull @JsonProperty(GENERATED_FROM) String templateIri,
      @Nonnull @JsonProperty(ACCESS_URL) String accessUrl) {
    return new AutoValue_SpreadsheetSchema(name, version, columnDescription, columnOrder, templateIri, accessUrl);
  }

  @Nonnull
  @JsonProperty(NAME)
  public abstract String getName();

  @Nonnull
  @JsonProperty(VERSION)
  public abstract String getVersion();

  @Nonnull
  @JsonProperty(COLUMN_DESCRIPTION)
  public abstract ImmutableMap<String, ColumnDescription> getColumnDescription();

  @Nonnull
  @JsonProperty(COLUMN_ORDER)
  public abstract ImmutableList<String> getColumnOrder();

  @Nonnull
  @JsonProperty(GENERATED_FROM)
  public abstract String getTemplateIri();

  @Nonnull
  @JsonProperty(ACCESS_URL)
  public abstract String getAccessUrl();

  @Nullable
  @JsonIgnore
  public ColumnDescription getColumnDescription(String columnName) {
    return getColumnDescription().get(columnName);
  }

  @Nonnull
  @JsonIgnore
  public Stream<ColumnDescription> getColumnDescriptionStream() {
    return getColumnDescription().values().stream();
  }

  @JsonIgnore
  public boolean containsColumn(String columnName) {
    return getColumnDescription().containsKey(columnName);
  }
}
