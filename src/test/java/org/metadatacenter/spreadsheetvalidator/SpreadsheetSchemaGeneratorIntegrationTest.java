package org.metadatacenter.spreadsheetvalidator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.reader.ArtifactReader;
import org.metadatacenter.spreadsheetvalidator.domain.PermissibleValue;
import org.metadatacenter.spreadsheetvalidator.domain.ValueType;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
class SpreadsheetSchemaGeneratorIntegrationTest {

  private final ObjectMapper mapper = new ObjectMapper();
  private ArtifactReader artifactReader = new ArtifactReader(mapper);
  private SpreadsheetSchemaGenerator spreadsheetSchemaGenerator = new SpreadsheetSchemaGenerator(artifactReader);
  private File templateFile;

  @BeforeEach
  void setUp() {
    var classLoader = getClass().getClassLoader();
    templateFile = new File(classLoader.getResource("cedar-template.jsonld").getFile());
  }

  @Test
  void shouldGenerateSpreadsheetSchema() throws IOException {
    var templateNode = (ObjectNode) mapper.readTree(templateFile);
    var spreadsheetSchema = spreadsheetSchemaGenerator.generateFrom(templateNode);
    assertThat(spreadsheetSchema.getName(), is("Sample Section"));
    assertThat(spreadsheetSchema.getTemplateIri(), is("https://repo.metadatacenter.org/templates/a9efb30e-4e2c-4d66-8890-b66204a4a774"));
    assertThat(spreadsheetSchema.getColumnDescriptions().size(), is(17));
    assertThat(spreadsheetSchema.getColumnDescriptions().keySet(), containsInAnyOrder(
        "sample_id",
        "source_storage_time_value",
        "source_storage_time_unit",
        "preparation_medium",
        "preparation_condition",
        "processing_time_value",
        "processing_time_unit",
        "storage_medium",
        "storage_condition",
        "quality_criteria",
        "histological_report",
        "section_index_number",
        "thickness_value",
        "thickness_unit",
        "area_value",
        "area_unit",
        "notes"
    ));
    var sampleId = spreadsheetSchema.getColumnDescription("sample_id");
    assertThat(sampleId.getColumnName(), is("sample_id"));
    assertThat(sampleId.getColumnLabel(), is("Sample ID"));
    assertThat(sampleId.getColumnDescription(), is("The HuBMAP ID for the sample assigned by the ingest portal. An example value might be \"HBM743.CKJW.876\""));
    assertThat(sampleId.getColumnType(), is(ValueType.STRING));
    assertThat(sampleId.getColumnSubType(), is(nullValue()));
    assertThat(sampleId.getMinValue(), is(nullValue()));
    assertThat(sampleId.getMaxValue(), is(nullValue()));
    assertThat(sampleId.getPermissibleValues(), is(empty()));

    var processingTimeValue = spreadsheetSchema.getColumnDescription("processing_time_value");
    assertThat(processingTimeValue.getColumnName(), is("processing_time_value"));
    assertThat(processingTimeValue.getColumnLabel(), is("Processing Time Value"));
    assertThat(processingTimeValue.getColumnDescription(), is("The time the tissue sample was being handled before the initial preservation."));
    assertThat(processingTimeValue.getColumnType(), is(ValueType.NUMBER));
    assertThat(processingTimeValue.getColumnSubType(), is(ValueType.DECIMAL));
    assertThat(processingTimeValue.getMinValue(), is(nullValue()));
    assertThat(processingTimeValue.getMaxValue(), is(nullValue()));
    assertThat(processingTimeValue.getPermissibleValues(), is(empty()));

    var processingTimeUnit = spreadsheetSchema.getColumnDescription("processing_time_unit");
    assertThat(processingTimeUnit.getColumnName(), is("processing_time_unit"));
    assertThat(processingTimeUnit.getColumnLabel(), is("Processing Time Unit"));
    assertThat(processingTimeUnit.getColumnDescription(), is("Unit of processing time measurement"));
    assertThat(processingTimeUnit.getColumnType(), is(ValueType.STRING));
    assertThat(processingTimeUnit.getColumnSubType(), is(nullValue()));
    assertThat(processingTimeUnit.getMinValue(), is(nullValue()));
    assertThat(processingTimeUnit.getMaxValue(), is(nullValue()));
    assertThat(processingTimeUnit.getPermissibleValues(), is(
        ImmutableList.of(
            PermissibleValue.create("Minute", "http://purl.obolibrary.org/obo/UO_0000031"),
            PermissibleValue.create("Hour", "http://purl.obolibrary.org/obo/UO_0000032"),
            PermissibleValue.create("Day", "http://purl.obolibrary.org/obo/UO_0000033")
        )));
  }
}