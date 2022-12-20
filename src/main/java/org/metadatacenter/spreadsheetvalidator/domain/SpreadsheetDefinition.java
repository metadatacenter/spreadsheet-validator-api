package org.metadatacenter.spreadsheetvalidator.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class SpreadsheetDefinition {

  private static final String NAME = "name";
  private static final String COLUMNS = "columns";

  private static final String GENERATED_FROM = "generatedFrom";

  @Nonnull
  @JsonCreator
  public static SpreadsheetDefinition create(
      @Nonnull @JsonProperty(NAME) String name,
      @Nonnull @JsonProperty(COLUMNS) ImmutableMap<String, ColumnDescription> columnDescriptions,
      @Nonnull @JsonProperty(GENERATED_FROM) String templateSource) {
    return new AutoValue_SpreadsheetDefinition(name, columnDescriptions, templateSource);
  }

  @Nonnull
  @JsonProperty(NAME)
  public abstract String getName();

  @Nonnull
  @JsonProperty(COLUMNS)
  public abstract ImmutableMap<String, ColumnDescription> getColumnDescription();

  @Nonnull
  @JsonProperty(GENERATED_FROM)
  public abstract String getTemplateSource();

  @Nonnull
  @JsonIgnore
  public Stream<ColumnDescription> getColumnDescriptionStream() {
    return getColumnDescription().values().stream();
  }
}
