package org.metadatacenter.spreadsheetvalidator.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.base.Strings;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ValidateSpreadsheetRequest {

  public static final String SPREADSHEET_DATA = "spreadsheetData";

  public static final String CEDAR_TEMPLATE_IRI = "cedarTemplateIri";

  @Nonnull
  @JsonCreator
  public static ValidateSpreadsheetRequest create(@Nonnull @JsonProperty(SPREADSHEET_DATA) List<Map<String, Object>> spreadsheetData,
                                                  @Nonnull @JsonProperty(CEDAR_TEMPLATE_IRI) String cedarTemplateIri) {
    return new AutoValue_ValidateSpreadsheetRequest(spreadsheetData, cedarTemplateIri);
  }

  @Nonnull
  @JsonProperty(SPREADSHEET_DATA)
  public abstract List<Map<String, Object>> getSpreadsheetData();

  @Nonnull
  @JsonProperty(CEDAR_TEMPLATE_IRI)
  public abstract String getCedarTemplateIri();

  public List<Map<String, Object>> getCheckedSpreadsheetData() {
    var spreadsheetData = getSpreadsheetData();
    if (spreadsheetData == null) {
      throw new SpreadsheetDataNotFoundException(
        "Bad request body.",
        new Exception("The input key '" + SPREADSHEET_DATA + "' is missing from the request body."), this);
    }
    return spreadsheetData;
  }

  public String getCheckedCedarTemplateIri() {
    var templateIri = getCedarTemplateIri();
    if (Strings.isNullOrEmpty(templateIri)) {
      throw new SchemaIdNotFoundException(
          "Bad request body.",
          new Exception("The input key '" + CEDAR_TEMPLATE_IRI + "' is missing from the request body."), this);
    }
    return templateIri;
  }
}
