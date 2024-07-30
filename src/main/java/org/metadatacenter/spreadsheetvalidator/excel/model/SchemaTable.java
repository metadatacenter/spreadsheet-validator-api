package org.metadatacenter.spreadsheetvalidator.excel.model;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class SchemaTable {

  @Nonnull
  public static SchemaTable create(@Nonnull Map<String, Map<String, Object>> records,
                                   @Nonnull SchemaKeyword schemaKeyword) {
    return new AutoValue_SchemaTable(records, schemaKeyword);
  }

  @Nonnull
  protected abstract Map<String, Map<String, Object>> getRecordMap();

  @Nonnull
  protected abstract SchemaKeyword getSchemaKeyword();

  @Nonnull
  public List<Map<String, Object>> getRecords() {
    return ImmutableList.copyOf(getRecordMap().values());
  }

  @Nonnull
  public List<String> getColumnNames() {
    return ImmutableList.copyOf(getRecordMap().keySet());
  }

  @Nonnull
  public Optional<Map<String, Object>> getSchemaFor(String columnName) {
    return Optional.ofNullable(getRecordMap().get(columnName));
  }

  @Nonnull
  public Optional<String> getVariableNameFor(String columnName) {
    return getSchemaFor(columnName)
        .map(schema -> (String) schema.get(getSchemaKeyword().ofVariable()))
        .or(() -> Optional.of(columnName));
  }

  @Nonnull
  public Optional<String> getVariableLabelFor(String columnName) {
    return getSchemaFor(columnName)
        .map(schema -> (String) schema.get(getSchemaKeyword().ofLabel()));
  }

  @Nonnull
  public Optional<String> getDatatypeFor(String columnName) {
    return getSchemaFor(columnName)
        .map(schema -> (String) schema.get(getSchemaKeyword().ofDatatype()));
  }

  @Nonnull
  public Optional<String> getRequirementLevelFor(String columnName) {
    return getSchemaFor(columnName)
        .map(schema -> (String) schema.get(getSchemaKeyword().ofRequirementLevel()));
  }

  @Nonnull
  public Optional<String> getDescriptionFor(String columnName) {
    return getSchemaFor(columnName)
        .map(schema -> (String) schema.get(getSchemaKeyword().ofDescription()));
  }

  @Nonnull
  public Optional<String> getInputExampleFor(String columnName) {
    return getSchemaFor(columnName)
        .map(schema -> (String) schema.get(getSchemaKeyword().ofInputExample()));
  }

  @Nonnull
  public Optional<Number> getMinValueConstraintFor(String columnName) {
    return getSchemaFor(columnName)
        .map(schema -> (Number) schema.get(getSchemaKeyword().ofMinValue()));
  }

  @Nonnull
  public Optional<Number> getMaxValueConstraintFor(String columnName) {
    return getSchemaFor(columnName)
        .map(schema -> (Number) schema.get(getSchemaKeyword().ofMaxValue()));
  }

  @Nonnull
  public Optional<String> getInputPatternFor(String columnName) {
    return getSchemaFor(columnName)
        .map(schema -> (String) schema.get(getSchemaKeyword().ofInputPattern()));
  }

  @Nonnull
  public Optional<String> getPermissibleValuesFor(String columnName) {
    return getSchemaFor(columnName)
        .map(schema -> (String) schema.get(getSchemaKeyword().ofPermissibleValues()));
  }
}
