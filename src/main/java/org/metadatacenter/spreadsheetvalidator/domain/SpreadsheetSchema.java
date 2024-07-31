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

  private static final String REQUIRED_COLUMNS = "requiredColumns";

  private static final String COLUMN_ORDER = "columnOrder";

  private static final String GENERATED_FROM = "generatedFrom";

  private static final String ACCESS_URL = "accessUrl";

  @Nonnull
  @JsonCreator
  public static SpreadsheetSchema create(
      @Nonnull @JsonProperty(NAME) String name,
      @Nonnull @JsonProperty(VERSION) String version,
      @Nonnull @JsonProperty(COLUMN_DESCRIPTION) ImmutableMap<String, ColumnDescription> columnDescription,
      @Nonnull @JsonProperty(REQUIRED_COLUMNS) ImmutableList<String> requiredColumns,
      @Nonnull @JsonProperty(COLUMN_ORDER) ImmutableList<String> columnOrder,
      @Nullable @JsonProperty(GENERATED_FROM) String templateIri,
      @Nullable @JsonProperty(ACCESS_URL) String accessUrl) {
    return new AutoValue_SpreadsheetSchema(name, version, columnDescription, requiredColumns, columnOrder, templateIri, accessUrl);
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
  @JsonProperty(REQUIRED_COLUMNS)
  public abstract ImmutableList<String> getRequiredColumns();

  @Nonnull
  @JsonProperty(COLUMN_ORDER)
  public abstract ImmutableList<String> getColumnOrder();

  @Nullable
  @JsonProperty(GENERATED_FROM)
  public abstract String getTemplateIri();

  @Nullable
  @JsonProperty(ACCESS_URL)
  public abstract String getAccessUrl();

  @Nullable
  @JsonIgnore
  public ColumnDescription getColumnDescription(String columnName) {
    // First try: search the column description by the map index key
    var columnDescription = getColumnDescription().get(columnName);
    if (columnDescription != null) {
      return columnDescription;
    }
    // Second try: search the column description by iterating over all the items and comparing each column label.
    for (var columnDescriptionItem : getColumnDescription().values()) {
      if (columnName.equals(columnDescriptionItem.getColumnLabel())) {
        return columnDescriptionItem;
      }
    }
    // No luck, return null.
    return null;
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
