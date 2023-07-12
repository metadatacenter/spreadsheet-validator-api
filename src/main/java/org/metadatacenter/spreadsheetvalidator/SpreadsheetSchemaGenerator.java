package org.metadatacenter.spreadsheetvalidator;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.metadatacenter.artifacts.model.core.FieldInputType;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.LiteralValueConstraint;
import org.metadatacenter.artifacts.model.core.NumberType;
import org.metadatacenter.artifacts.model.core.SchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ValueConstraints;
import org.metadatacenter.artifacts.model.core.Version;
import org.metadatacenter.artifacts.model.reader.ArtifactReader;
import org.metadatacenter.spreadsheetvalidator.domain.ColumnDescription;
import org.metadatacenter.spreadsheetvalidator.domain.PermissibleValue;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetSchema;
import org.metadatacenter.spreadsheetvalidator.domain.ValueType;
import org.metadatacenter.spreadsheetvalidator.thirdparty.OntologyValue;
import org.metadatacenter.spreadsheetvalidator.thirdparty.TerminologyService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
  private final TerminologyService terminologyService;

  @Inject
  public SpreadsheetSchemaGenerator(@Nonnull ArtifactReader artifactReader,
                                    @Nonnull TerminologyService terminologyService) {
    this.artifactReader = checkNotNull(artifactReader);
    this.terminologyService = checkNotNull(terminologyService);
  }

  @Nonnull
  public SpreadsheetSchema generateFrom(@Nonnull ObjectNode templateNode) {
    var templateSchema = artifactReader.readTemplateSchemaArtifact(templateNode);
    var templateName = templateSchema.getName();
    var templateVersion = getTemplateVersion(templateSchema);
    var templateIri = templateSchema.getJsonLdId().toString();
    var templateAccessUrl = getTemplateAccessUrl(templateSchema);
    var fieldSchemas = templateSchema.getFieldSchemas();
    var columnDescription = fieldSchemas.values()
        .stream()
        .collect(new ColumnDescriptionCollector());
    var columnOrder = fieldSchemas.values()
        .stream()
        .map(SchemaArtifact::getName)
        .collect(ImmutableList.toImmutableList());
    return SpreadsheetSchema.create(templateName, templateVersion, columnDescription, columnOrder, templateIri, templateAccessUrl);
  }

  @Nonnull
  private String getTemplateVersion(TemplateSchemaArtifact templateSchema) {
    var defaultVersion = Version.fromString("0.0.1");
    return templateSchema.getVersion().orElse(defaultVersion).toString();
  }

  @Nonnull
  private String getTemplateAccessUrl(TemplateSchemaArtifact templateSchema) {
    var templateIri = templateSchema.getJsonLdId().get().toString();
    return String.format(
        "https://openview.metadatacenter.org/templates/%s",
        URLEncoder.encode(templateIri, StandardCharsets.UTF_8));
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
              getColumnSubType(fieldSchema.getValueConstraints().get().getNumberType()),
              fieldSchema.getValueConstraints().get().getMinValue().orElse(null),
              fieldSchema.getValueConstraints().get().getMaxValue().orElse(null),
              fieldSchema.getValueConstraints().get().isRequiredValue(),
              fieldSchema.getDescription(),
              getPermissibleValues(fieldSchema.getName(), fieldSchema.getValueConstraints().get())
          ));
    }

    @Nonnull
    private ValueType getColumnType(FieldInputType inputType) {
      var inputTypeText = inputType.getText();
      if ("numeric".equals(inputTypeText)) {
        return ValueType.NUMBER;
      } else if ("link".equals(inputTypeText)) {
        return ValueType.URL;
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

    @Nonnull
    private ImmutableList<PermissibleValue> getPermissibleValues(String fieldName, ValueConstraints valueConstraints) {
      if (valueConstraints.hasOntologyValueBasedConstraints()) {
        var ontologyValues = terminologyService.getOntologyValues(fieldName, valueConstraints);
        return ontologyValues.stream().collect(new OntologyValueCollector());
      } else {
        return valueConstraints.getLiterals().stream().collect(new LiteralValueCollector());
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

  class OntologyValueCollector implements Collector<
      OntologyValue,
      ImmutableList.Builder<PermissibleValue>,
      ImmutableList<PermissibleValue>> {

    @Override
    public Supplier<ImmutableList.Builder<PermissibleValue>> supplier() {
      return ImmutableList.Builder::new;
    }

    @Override
    public BiConsumer<ImmutableList.Builder<PermissibleValue>, OntologyValue> accumulator() {
      return (builder, ontologyValue) -> builder.add(
          PermissibleValue.create(
              ontologyValue.getPrefLabel(),
              ontologyValue.getIri())
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

  class LiteralValueCollector implements Collector<
      LiteralValueConstraint,
      ImmutableList.Builder<PermissibleValue>,
      ImmutableList<PermissibleValue>> {

    @Override
    public Supplier<ImmutableList.Builder<PermissibleValue>> supplier() {
      return ImmutableList.Builder::new;
    }

    @Override
    public BiConsumer<ImmutableList.Builder<PermissibleValue>, LiteralValueConstraint> accumulator() {
      return (builder, valueConstraint) -> builder.add(
          PermissibleValue.create(valueConstraint.getLabel(), null)
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
