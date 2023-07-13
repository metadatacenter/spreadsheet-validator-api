package org.metadatacenter.spreadsheetvalidator.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ColumnDescription {

  private static final String NAME = "name";
  private static final String LABEL = "label";
  private static final String TYPE = "type";
  private static final String SUB_TYPE = "subType";
  private static final String MIN = "min";
  private static final String MAX = "max";
  private static final String REQUIRED = "required";
  private static final String PERMISSIBLE_VALUES = "permissibleValues";
  private static final String DESCRIPTION = "description";
  private static final String EXAMPLE = "example";

  @Nonnull
  @JsonCreator
  public static ColumnDescription create(@Nonnull @JsonProperty(NAME) String columnName,
                                         @Nonnull @JsonProperty(LABEL) String columnLabel,
                                         @Nonnull @JsonProperty(TYPE) ValueType valueType,
                                         @Nullable @JsonProperty(SUB_TYPE) ValueType valueSubType,
                                         @Nullable @JsonProperty(MIN) Number minValue,
                                         @Nullable @JsonProperty(MAX) Number maxValue,
                                         @Nonnull @JsonProperty(REQUIRED) Boolean isRequiredColumn,
                                         @Nonnull @JsonProperty(DESCRIPTION) String columnDescription,
                                         @Nullable @JsonProperty(EXAMPLE) String valueExample,
                                         @Nonnull @JsonProperty(PERMISSIBLE_VALUES) ImmutableList<PermissibleValue> permissibleValues) {
    return new AutoValue_ColumnDescription(columnName, columnLabel, valueType, valueSubType, minValue, maxValue,
        isRequiredColumn, columnDescription, valueExample, permissibleValues);
  }

  @Nonnull
  @JsonProperty(NAME)
  public abstract String getColumnName();

  @Nonnull
  @JsonProperty(LABEL)
  public abstract String getColumnLabel();

  @Nonnull
  @JsonProperty(TYPE)
  public abstract ValueType getColumnType();

  @Nullable
  @JsonProperty(SUB_TYPE)
  public abstract ValueType getColumnSubType();

  @JsonIgnore
  public boolean hasSubType() {
    return getColumnSubType() != null;
  }

  @Nullable
  @JsonProperty(MIN)
  public abstract Number getMinValue();

  @JsonIgnore
  public boolean hasMinValue() {
    return getMinValue() != null;
  }

  @Nullable
  @JsonProperty(MAX)
  public abstract Number getMaxValue();

  @JsonIgnore
  public boolean hasMaxValue() {
    return getMaxValue() != null;
  }

  @Nonnull
  @JsonProperty(REQUIRED)
  public abstract Boolean isRequiredColumn();

  @Nonnull
  @JsonProperty(DESCRIPTION)
  public abstract String getColumnDescription();

  @Nullable
  @JsonProperty(EXAMPLE)
  public abstract String getValueExample();

  @JsonIgnore
  public boolean hasValueExample() {
    return getValueExample() != null;
  }

  @Nonnull
  @JsonProperty(PERMISSIBLE_VALUES)
  public abstract ImmutableList<PermissibleValue> getPermissibleValues();

  @JsonIgnore
  public boolean hasPermissibleValues() {
    return !getPermissibleValues().isEmpty();
  }
}
