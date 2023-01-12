package org.metadatacenter.spreadsheetvalidator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Null;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
class SpreadsheetInputGeneratorIntegrationTest {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private SpreadsheetInputGenerator spreadsheetInputGenerator = new SpreadsheetInputGenerator(objectMapper);
  private File spreadsheetFile;

  @BeforeEach
  void setUp() throws IOException {
    var classLoader = getClass().getClassLoader();
    spreadsheetFile = new File(classLoader.getResource("tissue-sample-spreadsheet.xlsx").getFile());
  }

  @Test
  void shouldGenerateSpreadsheetInput() throws IOException {
    var fis = new FileInputStream(spreadsheetFile);
    var spreadsheet = spreadsheetInputGenerator.generateFrom(fis);

    assertThat(spreadsheet.size(), is(20)); // row size
    assertThat(spreadsheet.get(0).size(), is(18)); // column size
    assertThat(spreadsheet.get(0).get("sample_ID"), is("Visium_9OLC_A4_S1"));
    assertThat(spreadsheet.get(1).get("sample_ID"), is("Visium_9OLC_A4_S2"));
    assertThat(spreadsheet.get(2).get("sample_ID"), is("Visium_9OLC_I4_S1"));
    assertThat(spreadsheet.get(3).get("sample_ID"), is("Visium_9OLC_I4_S2"));
    assertThat(spreadsheet.get(4).get("sample_ID"), is(nullValue()));
    assertThat(spreadsheet.get(5).get("sample_ID"), is(nullValue()));
    assertThat(spreadsheet.get(6).get("sample_ID"), is(nullValue()));
    assertThat(spreadsheet.get(7).get("sample_ID"), is(nullValue()));
    assertThat(spreadsheet.get(8).get("sample_ID"), is(nullValue()));
    assertThat(spreadsheet.get(9).get("sample_ID"), is("Visium_40AZ_Q9_S1"));
    assertThat(spreadsheet.get(10).get("sample_ID"), is("Visium_40AZ_Q9_S2"));
    assertThat(spreadsheet.get(11).get("sample_ID"), is("Visium_40AZ_Q9_S3"));
    assertThat(spreadsheet.get(12).get("sample_ID"), is("Visium_40AZ_Q9_S4"));
    assertThat(spreadsheet.get(13).get("sample_ID"), is("Visium_90LC_W3_S1"));
    assertThat(spreadsheet.get(14).get("sample_ID"), is("Visium_90LC_W3_S2"));
    assertThat(spreadsheet.get(15).get("sample_ID"), is("Visium_90LC_W3_S3"));
    assertThat(spreadsheet.get(16).get("sample_ID"), is("Visium_90LC_W3_S4"));
    assertThat(spreadsheet.get(17).get("sample_ID"), is("Visium_90LC_W3_S5"));
    assertThat(spreadsheet.get(18).get("sample_ID"), is("Visium_90LC_W3_S6"));
    assertThat(spreadsheet.get(19).get("sample_ID"), is("Visium_90LC_W3_S7"));

    assertThat(spreadsheet.get(0).get("processing_time_value"), is(4));
    assertThat(spreadsheet.get(1).get("processing_time_value"), is(4));
    assertThat(spreadsheet.get(2).get("processing_time_value"), is(4));
    assertThat(spreadsheet.get(3).get("processing_time_value"), is(4));
    assertThat(spreadsheet.get(4).get("processing_time_value"), is("10 minutes"));
    assertThat(spreadsheet.get(5).get("processing_time_value"), is("10 minutes"));
    assertThat(spreadsheet.get(6).get("processing_time_value"), is("10 minutes"));
    assertThat(spreadsheet.get(7).get("processing_time_value"), is("10 minutes"));
    assertThat(spreadsheet.get(8).get("processing_time_value"), is("10 minutes"));
    assertThat(spreadsheet.get(9).get("processing_time_value"), is(5));
    assertThat(spreadsheet.get(10).get("processing_time_value"), is(5));
    assertThat(spreadsheet.get(11).get("processing_time_value"), is(5));
    assertThat(spreadsheet.get(12).get("processing_time_value"), is(5));
    assertThat(spreadsheet.get(13).get("processing_time_value"), is(3));
    assertThat(spreadsheet.get(14).get("processing_time_value"), is(3));
    assertThat(spreadsheet.get(15).get("processing_time_value"), is(3));
    assertThat(spreadsheet.get(16).get("processing_time_value"), is(3));
    assertThat(spreadsheet.get(17).get("processing_time_value"), is(4));
    assertThat(spreadsheet.get(18).get("processing_time_value"), is(4));
    assertThat(spreadsheet.get(19).get("processing_time_value"), is(4));
  }
}