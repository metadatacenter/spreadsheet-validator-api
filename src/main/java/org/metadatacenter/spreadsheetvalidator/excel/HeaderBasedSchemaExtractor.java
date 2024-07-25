package org.metadatacenter.spreadsheetvalidator.excel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.metadatacenter.spreadsheetvalidator.domain.ColumnDescription;
import org.metadatacenter.spreadsheetvalidator.domain.PermissibleValue;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetSchema;
import org.metadatacenter.spreadsheetvalidator.domain.ValueType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class HeaderBasedSchemaExtractor {

  // Reserved field names
  private static final String VARIABLE = "variable";
  private static final String TYPE = "type";
  private static final String PRIORITY = "priority";
  private static final String DESCRIPTION = "description";
  private static final String MIN_VALUE = "min_value";
  private static final String MAX_VALUE = "max_value";
  private static final String INPUT_PATTERN = "input_pattern";
  private static final String PERMISSIBLE_VALUES = "permissible_values";

  // Mapping from supported types to a ValueType object
  private static final Map<String, ValueType> TYPE_MAP = ImmutableMap.<String, ValueType>builder()
      .put("text", ValueType.STRING)
      .put("decimal", ValueType.NUMBER)
      .put("integer", ValueType.NUMBER)
      .put("float", ValueType.NUMBER)
      .put("double", ValueType.NUMBER)
      .put("email", ValueType.STRING)
      .put("phone", ValueType.STRING)
      .put("doi", ValueType.STRING)
      .put("orcid", ValueType.STRING)
      .put("date", ValueType.STRING)
      .put("time", ValueType.STRING)
      .put("datetime", ValueType.STRING)
      .put("boolean", ValueType.STRING)
      .put("obo id", ValueType.STRING)
      .put("rrid", ValueType.STRING)
      .put("ror", ValueType.STRING)
      .put("url", ValueType.URL)
      .build();

  private static final Map<String, ValueType> SUBTYPE_MAP = ImmutableMap.<String, ValueType>builder()
      .put("decimal", ValueType.DECIMAL)
      .put("integer", ValueType.INTEGER)
      .build();

  // Mapping from supported compliance type to a boolean value
  private static final Map<String, Boolean> PRIORITY_MAP = ImmutableMap.<String, Boolean>builder()
      .put("REQUIRED", true)
      .put("OPTIONAL", false)
      .put("RECOMMENDED", false)
      .build();

  // Mapping from supported types to a specific regex pattern
  private static final Map<String, String> INPUT_PATTERN_MAP = ImmutableMap.<String, String>builder()
      .put("email", "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
      .put("phone", "^\\+?[0-9. ()-]{10,15}$")
      .put("doi", "^10\\.\\d{4,9}/[-._;()/:a-zA-Z0-9]+$")
      .put("orcid", "^\\d{4}-\\d{4}-\\d{4}-\\d{3}[\\dX]$")
      .put("date", "^\\d{4}-\\d{2}-\\d{2}$")  // YYYY-MM-DD
      .put("time", "^\\d{2}:\\d{2}:\\d{2}$")  // hh:mm
      .put("datetime", "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}:\\d{2}(?:Z|[+-]\\d{2}:\\d{2})?$")  // YYYY-MM-DDThh:mm:ss, with optional timezone
      .put("boolean", "^(?i:true|false)$")
      .put("obo id", "^[A-Za-z]+:[0-9]{7}$")
      .put("rrid", "^RRID:[A-Za-z]+_[0-9]+$")
      .put("ror", "^ROR:0[a-hj-km-np-tv-z|0-9]{6}[0-9]{2}$")
      .build();

  public SpreadsheetSchema extractFrom(@Nonnull DataSheet dataSheet) {
    var dataSchemaTable = dataSheet.getUncheckedSchemaTable();
    var dataRecordTable = dataSheet.getDataRecordTable();
    var headerColumnNames = dataRecordTable.getHeaderColumnNames();
    return SpreadsheetSchema.create(
        "virtual-schema",
        "1.0.0",
        getColumnDescriptionMap(dataSchemaTable, headerColumnNames),
        getRequiredColumns(dataSchemaTable, headerColumnNames),
        getColumnOrder(dataSchemaTable, headerColumnNames),
        null,
        null);
  }

  private ImmutableMap<String, ColumnDescription> getColumnDescriptionMap(DataSchemaTable dataSchemaTable,
                                                                          List<String> headerColumnNames) {
    return IntStream.range(1, dataSchemaTable.columnLength())
        .mapToObj(i -> {
          var columnSchema = dataSchemaTable.getColumn(i);
          var variableLabel = headerColumnNames.get(i);
          var variableName = (String) columnSchema.getOrDefault(VARIABLE, variableLabel);
          var variableType = TYPE_MAP.get((String) columnSchema.get(TYPE));
          var variableSubType = SUBTYPE_MAP.get((String) columnSchema.get(TYPE));
          var isRequired = PRIORITY_MAP.get((String) columnSchema.get(PRIORITY));
          var description = (String) columnSchema.get(DESCRIPTION);
          var minValue = (Number) columnSchema.get(MIN_VALUE);
          var maxValue = (Number) columnSchema.get(MAX_VALUE);
          var inputPattern = getInputPattern(columnSchema);
          var permissibleValues = getPermissibleValues(columnSchema);
          var example = "";
          return Map.entry(variableName, ColumnDescription.create(
              variableName, variableLabel,
              variableType, variableSubType,
              minValue, maxValue,
              isRequired, description,
              example, inputPattern,
              permissibleValues
          ));
        })
        .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private static String getInputPattern(Map<String, Object> columnSchema) {
    var inputPattern = (String) columnSchema.get(INPUT_PATTERN);
    if (inputPattern != null) {
      return inputPattern;
    }
    return INPUT_PATTERN_MAP.get((String) columnSchema.get(TYPE));
  }

  private ImmutableList<PermissibleValue> getPermissibleValues(Map<String, Object> columnSchema) {
    String values = (String) columnSchema.get(PERMISSIBLE_VALUES);
    if (values == null) {
      return ImmutableList.of();
    }
    return Stream.of(values.split("\\|"))
        .map(s -> PermissibleValue.create(s.trim()))
        .collect(ImmutableList.toImmutableList());
  }

  private ImmutableList<String> getRequiredColumns(DataSchemaTable dataSchemaTable, List<String> headerColumnNames) {
    return IntStream.range(1, dataSchemaTable.columnLength())
        .mapToObj(i -> {
          var columnSchema = dataSchemaTable.getColumn(i);
          var variableLabel = headerColumnNames.get(i);
          var variableName = (String) columnSchema.getOrDefault(VARIABLE, variableLabel);
          var isRequired = PRIORITY_MAP.get((String) columnSchema.get(PRIORITY));
          return isRequired ? variableName : null;
        })
        .filter(Objects::nonNull)
        .collect(ImmutableList.toImmutableList());
  }

  private ImmutableList<String> getColumnOrder(DataSchemaTable dataSchemaTable, List<String> headerColumnNames) {
    return IntStream.range(1, dataSchemaTable.columnLength())
        .mapToObj(i -> {
          var columnSchema = dataSchemaTable.getColumn(i);
          var variableLabel = headerColumnNames.get(i);
          return (String) columnSchema.getOrDefault(VARIABLE, variableLabel);
        })
        .collect(ImmutableList.toImmutableList());
  }
}
