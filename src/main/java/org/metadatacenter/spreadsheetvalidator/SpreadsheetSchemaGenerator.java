package org.metadatacenter.spreadsheetvalidator;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.metadatacenter.artifacts.model.core.ClassValueConstraint;
import org.metadatacenter.artifacts.model.core.FieldInputType;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.NumberType;
import org.metadatacenter.artifacts.model.reader.ArtifactReader;
import org.metadatacenter.spreadsheetvalidator.domain.ColumnDescription;
import org.metadatacenter.spreadsheetvalidator.domain.PermissibleValue;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetSchema;
import org.metadatacenter.spreadsheetvalidator.domain.ValueType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class SpreadsheetSchemaGenerator {

  private final ArtifactReader artifactReader;

  @Inject
  public SpreadsheetSchemaGenerator(@Nonnull ArtifactReader artifactReader) {
    this.artifactReader = checkNotNull(artifactReader);
  }

  @Nonnull
  public SpreadsheetSchema generateFrom(@Nonnull ObjectNode templateNode) {
    var templateSchema = artifactReader.readTemplateSchemaArtifact(templateNode);
    var templateName = templateSchema.getName();
    var templateIri = templateSchema.getJsonLDID().toString();
    var fieldSchemas = templateSchema.getFieldSchemas();
    var columnDescriptions = fieldSchemas.values()
        .stream()
        .collect(new ColumnDescriptionCollector());
    return SpreadsheetSchema.create(templateName, columnDescriptions, templateIri);
  }

  class ColumnDescriptionCollector implements Collector<
        FieldSchemaArtifact,
        ImmutableMap.Builder<String, ColumnDescription>,
        ImmutableMap<String, ColumnDescription>> {

    @Override
    public Supplier<ImmutableMap.Builder<String, ColumnDescription>> supplier() {
      return ImmutableMap.Builder::new;
    }

    @Override
    public BiConsumer<ImmutableMap.Builder<String, ColumnDescription>, FieldSchemaArtifact> accumulator() {
      return (builder, fieldSchema) -> builder.put(fieldSchema.getName(),
          ColumnDescription.create(
              fieldSchema.getName(),
              fieldSchema.getSkosPrefLabel().orElse(fieldSchema.getName()),
              getColumnType(fieldSchema.getFieldUI().getInputType()),
              getColumnSubType(fieldSchema.getValueConstraints().getNumberType()),
              fieldSchema.getValueConstraints().getMinValue().orElse(null),
              fieldSchema.getValueConstraints().getMaxValue().orElse(null),
              fieldSchema.getValueConstraints().isRequiredValue(),
              fieldSchema.getDescription(),
              fieldSchema.getValueConstraints().getClasses()
                  .stream()
                  .collect(new PermissibleValueCollector())
          ));
    }

    @Nonnull
    private ValueType getColumnType(FieldInputType inputType) {
      var inputTypeText = inputType.getText();
      if("numeric".equals(inputTypeText)) {
        return ValueType.NUMBER;
      } else {
        return ValueType.STRING;
      }
    }

    @Nullable
    private ValueType getColumnSubType(Optional<NumberType> numberType) {
      if (numberType.isPresent()) {
        var numberTypeText = numberType.get().getText();
        if ("xsd:int".equals(numberTypeText)) {
          return ValueType.INTEGER;
        } else {
          return ValueType.DECIMAL;
        }
      } else {
        return null;
      }
    }

    @Override
    public BinaryOperator<ImmutableMap.Builder<String, ColumnDescription>> combiner() {
      return (builder1, builder2) -> {
        builder1.putAll(builder2.build());
        return builder1;
      };
    }

    @Override
    public Function<ImmutableMap.Builder<String, ColumnDescription>, ImmutableMap<String, ColumnDescription>> finisher() {
      return ImmutableMap.Builder::build;
    }

    @Override
    public Set<Characteristics> characteristics() {
      return ImmutableSet.of();
    }
  }

  class PermissibleValueCollector implements Collector<
      ClassValueConstraint,
      ImmutableList.Builder<PermissibleValue>,
      ImmutableList<PermissibleValue>> {

    @Override
    public Supplier<ImmutableList.Builder<PermissibleValue>> supplier() {
      return ImmutableList.Builder::new;
    }

    @Override
    public BiConsumer<ImmutableList.Builder<PermissibleValue>, ClassValueConstraint> accumulator() {
      return (builder, valueConstraint) -> builder.add(
          PermissibleValue.create(
              valueConstraint.getPrefLabel(),
              valueConstraint.getUri().toString())
      );
    }

    @Override
    public BinaryOperator<ImmutableList.Builder<PermissibleValue>> combiner() {
      return (builder1, builder2) -> {
        builder1.addAll(builder2.build());
        return builder1;
      };
    }

    @Override
    public Function<ImmutableList.Builder<PermissibleValue>, ImmutableList<PermissibleValue>> finisher() {
      return ImmutableList.Builder::build;
    }

    @Override
    public Set<Characteristics> characteristics() {
      return ImmutableSet.of();
    }
  }
}
