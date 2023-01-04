package org.metadatacenter.spreadsheetvalidator.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class SpreadsheetSchema {

  private static final String NAME = "name";
  private static final String COLUMNS = "columns";

  private static final String GENERATED_FROM = "generatedFrom";

  @Nonnull
  @JsonCreator
  public static SpreadsheetSchema create(
      @Nonnull @JsonProperty(NAME) String name,
      @Nonnull @JsonProperty(COLUMNS) ImmutableMap<String, ColumnDescription> columnDescriptions,
      @Nonnull @JsonProperty(GENERATED_FROM) String templateIri) {
    return new AutoValue_SpreadsheetSchema(name, columnDescriptions, templateIri);
  }

  @Nonnull
  @JsonProperty(NAME)
  public abstract String getName();

  @Nonnull
  @JsonProperty(COLUMNS)
  public abstract ImmutableMap<String, ColumnDescription> getColumnDescriptions();

  @Nonnull
  @JsonProperty(GENERATED_FROM)
  public abstract String getTemplateIri();

  @Nonnull
  @JsonIgnore
  public ColumnDescription getColumnDescription(String columnName) {
    return getColumnDescriptions().get(columnName);
  }

  @Nonnull
  @JsonIgnore
  public Stream<ColumnDescription> getColumnDescriptionStream() {
    return getColumnDescriptions().values().stream();
  }
}
