package org.metadatacenter.spreadsheetvalidator.excel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import jakarta.inject.Inject;
import org.metadatacenter.spreadsheetvalidator.domain.ColumnDescription;
import org.metadatacenter.spreadsheetvalidator.domain.PermissibleValue;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetSchema;
import org.metadatacenter.spreadsheetvalidator.excel.model.BuiltinTypeMap;
import org.metadatacenter.spreadsheetvalidator.excel.model.RequirementLevelMap;
import org.metadatacenter.spreadsheetvalidator.excel.model.ReservedKeyword;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ExcelBasedSchemaParser {

  private final ReservedKeyword reservedKeyword;

  private final BuiltinTypeMap builtinTypeMap;

  private final RequirementLevelMap requirementLevelMap;

  @Inject
  public ExcelBasedSchemaParser(@Nonnull ReservedKeyword reservedKeyword,
                                @Nonnull BuiltinTypeMap builtinTypeMap,
                                @Nonnull RequirementLevelMap requirementLevelMap) {
    this.reservedKeyword = checkNotNull(reservedKeyword);
    this.builtinTypeMap = checkNotNull(builtinTypeMap);
    this.requirementLevelMap = checkNotNull(requirementLevelMap);
  }

  public SpreadsheetSchema extractTableSchemaFrom(@Nonnull DataSheet dataSheet) {
    var dataSchemaTable = dataSheet.getUncheckedSchemaTable();
    var dataRecordTable = dataSheet.getDataRecordTable();
    var headerColumnNames = dataRecordTable.getHeaderColumnNames();
    return SpreadsheetSchema.create(
        "virtual-schema",
        "1.0.0",
        getColumnDescriptions(dataSchemaTable, headerColumnNames),
        getRequiredColumns(dataSchemaTable, headerColumnNames),
        getColumnOrder(dataSchemaTable, headerColumnNames),
        null,
        null);
  }

  private ImmutableMap<String, ColumnDescription> getColumnDescriptions(DataSchemaTable dataSchemaTable,
                                                                        List<String> headerColumnNames) {
    return IntStream.range(1, dataSchemaTable.columnLength())
        .mapToObj(i -> {
          var columnSchema = dataSchemaTable.getColumn(i);
          var variableLabel = headerColumnNames.get(i);
          var variableName = (String) columnSchema.getOrDefault(reservedKeyword.ofVariable(), variableLabel);
          var variableType = builtinTypeMap.getBuiltinType((String) columnSchema.getOrDefault(reservedKeyword.ofDatatype(), "text")).getType();
          var variableSubType = builtinTypeMap.getBuiltinType((String) columnSchema.getOrDefault(reservedKeyword.ofDatatype(), "text")).getSubType();
          var isRequired = requirementLevelMap.isRequired((String) columnSchema.getOrDefault(reservedKeyword.ofRequirementLevel(), "optional"));
          var description = (String) columnSchema.get(reservedKeyword.ofDescription());
          var minValue = (Number) columnSchema.get(reservedKeyword.ofMinValue());
          var maxValue = (Number) columnSchema.get(reservedKeyword.ofMaxValue());
          var inputPattern = getInputPattern(columnSchema);
          var permissibleValues = getPermissibleValues(columnSchema);
          var inputExample = getInputExample(columnSchema);
          return Map.entry(variableName, ColumnDescription.create(
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


  @Nullable
  private String getInputExample(Map<String, Object> columnSchema) {
    var inputExample = (String) columnSchema.get(reservedKeyword.ofInputExample());
    return (inputExample != null)
        ? inputExample
        : builtinTypeMap.getBuiltinType((String) columnSchema.get(reservedKeyword.ofDatatype())).getInputExample();
  }

  @Nullable
  private String getInputPattern(Map<String, Object> columnSchema) {
    var inputPattern = (String) columnSchema.get(reservedKeyword.ofInputPattern());
    return (inputPattern != null)
        ? inputPattern
        : builtinTypeMap.getBuiltinType((String) columnSchema.get(reservedKeyword.ofDatatype())).getInputPattern();
  }

  @Nonnull
  private ImmutableList<PermissibleValue> getPermissibleValues(Map<String, Object> columnSchema) {
    String values = (String) columnSchema.get(reservedKeyword.ofPermissibleValues());
    if (values == null) {
      return ImmutableList.of();
    }
    return Stream.of(values.split("\\|"))
        .map(s -> PermissibleValue.create(s.trim()))
        .collect(ImmutableList.toImmutableList());
  }

  @Nonnull
  private ImmutableList<String> getRequiredColumns(DataSchemaTable dataSchemaTable, List<String> headerColumnNames) {
    return IntStream.range(1, dataSchemaTable.columnLength())
        .mapToObj(i -> {
          var columnSchema = dataSchemaTable.getColumn(i);
          var variableLabel = headerColumnNames.get(i);
          var variableName = (String) columnSchema.getOrDefault(reservedKeyword.ofVariable(), variableLabel);
          var isRequired = requirementLevelMap.isRequired((String) columnSchema.get(reservedKeyword.ofRequirementLevel()));
          return isRequired ? variableName : null;
        })
        .filter(Objects::nonNull)
        .collect(ImmutableList.toImmutableList());
  }

  @Nonnull
  private ImmutableList<String> getColumnOrder(DataSchemaTable dataSchemaTable, List<String> headerColumnNames) {
    return IntStream.range(1, dataSchemaTable.columnLength())
        .mapToObj(i -> {
          var columnSchema = dataSchemaTable.getColumn(i);
          var variableLabel = headerColumnNames.get(i);
          return (String) columnSchema.getOrDefault(reservedKeyword.ofVariable(), variableLabel);
        })
        .collect(ImmutableList.toImmutableList());
  }
}
