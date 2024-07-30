package org.metadatacenter.spreadsheetvalidator.excel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import jakarta.inject.Inject;
import org.metadatacenter.spreadsheetvalidator.domain.ColumnDescription;
import org.metadatacenter.spreadsheetvalidator.domain.PermissibleValue;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetSchema;
import org.metadatacenter.spreadsheetvalidator.excel.model.BuiltinTypeMap;
import org.metadatacenter.spreadsheetvalidator.excel.model.RequirementLevelMap;
import org.metadatacenter.spreadsheetvalidator.excel.model.SchemaTable;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ExcelBasedSchemaParser {

  private final BuiltinTypeMap builtinTypeMap;

  private final RequirementLevelMap requirementLevelMap;

  @Inject
  public ExcelBasedSchemaParser(@Nonnull BuiltinTypeMap builtinTypeMap,
                                @Nonnull RequirementLevelMap requirementLevelMap) {
    this.builtinTypeMap = checkNotNull(builtinTypeMap);
    this.requirementLevelMap = checkNotNull(requirementLevelMap);
  }

  public SpreadsheetSchema extractTableSchemaFrom(@Nonnull SchemaTable schemaTable) {
    return SpreadsheetSchema.create(
        "virtual-schema",
        "1.0.0",
        getColumnDescriptions(schemaTable),
        getRequiredColumns(schemaTable),
        getColumnOrder(schemaTable),
        null,
        null);
  }

  private ImmutableMap<String, ColumnDescription> getColumnDescriptions(SchemaTable schemaTable) {
    return schemaTable.getColumnNames().stream()
        .map(columnName -> {
          var variableName = schemaTable.getVariableNameFor(columnName).orElse(null);
          var variableLabel = schemaTable.getVariableLabelFor(columnName).orElse(null);
          var variableType = builtinTypeMap.getBuiltinType(schemaTable.getDatatypeFor(columnName).orElse("text")).getType();
          var variableSubType = builtinTypeMap.getBuiltinType(schemaTable.getDatatypeFor(columnName).orElse("text")).getSubType();
          var minValue = schemaTable.getMinValueConstraintFor(columnName).orElse(null);
          var maxValue = schemaTable.getMaxValueConstraintFor(columnName).orElse(null);
          var isRequired = requirementLevelMap.isRequired(schemaTable.getRequirementLevelFor(columnName).orElse("OPTIONAL"));
          var description = schemaTable.getDescriptionFor(columnName).orElse(null);
          var inputExample = getInputExample(schemaTable, columnName).orElse(null);
          var inputPattern = getInputPattern(schemaTable, columnName).orElse(null);
          var permissibleValues = getPermissibleValues(schemaTable, columnName);
          return Map.entry(variableName,
              ColumnDescription.create(
                  variableName, variableLabel,
                  variableType, variableSubType,
                  minValue, maxValue,
                  isRequired, description,
                  inputExample, inputPattern,
                  permissibleValues
              ));
        })
        .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @Nonnull
  private Optional<String> getInputExample(SchemaTable schemaTable, String columnName) {
    return schemaTable.getInputExampleFor(columnName)
        .or(() -> {
          var datatype = schemaTable.getDatatypeFor(columnName).orElse("text");
          return Optional.ofNullable(builtinTypeMap.getBuiltinType(datatype).getInputExample());
        });
  }

  @Nonnull
  private Optional<String> getInputPattern(SchemaTable schemaTable, String columnName) {
    return schemaTable.getInputPatternFor(columnName)
        .or(() -> {
          var datatype = schemaTable.getDatatypeFor(columnName).orElse("text");
          return Optional.ofNullable(builtinTypeMap.getBuiltinType(datatype).getInputPattern());
        });
  }

  @Nonnull
  private ImmutableList<PermissibleValue> getPermissibleValues(SchemaTable schemaTable, String columnName) {
    return schemaTable.getPermissibleValuesFor(columnName)
        .map(values -> Stream.of(values.split("\\|"))
            .map(s -> PermissibleValue.create(s.trim()))
            .collect(ImmutableList.toImmutableList()))
        .orElseGet(ImmutableList::of);
  }

  @Nonnull
  private ImmutableList<String> getRequiredColumns(SchemaTable schemaTable) {
    return schemaTable.getColumnNames().stream()
        .filter(columnName -> {
          var requirementLevel = schemaTable.getRequirementLevelFor(columnName).orElse("OPTIONAL");
          return requirementLevelMap.isRequired(requirementLevel);
        })
        .collect(ImmutableList.toImmutableList());
  }

  @Nonnull
  private ImmutableList<String> getColumnOrder(SchemaTable schemaTable) {
    return ImmutableList.copyOf(schemaTable.getColumnNames());
  }
}
