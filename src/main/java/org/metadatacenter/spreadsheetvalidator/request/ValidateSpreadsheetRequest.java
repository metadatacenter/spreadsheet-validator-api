package org.metadatacenter.spreadsheetvalidator.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.metadatacenter.spreadsheetvalidator.domain.Spreadsheet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

  public static final String ALLOW_ADDITIONAL_COLUMNS = "allowAdditionalColumns";

  @Nonnull
  @JsonCreator
  public static ValidateSpreadsheetRequest create(@Nonnull @JsonProperty(SPREADSHEET_DATA) List<Map<String, Object>> spreadsheetData,
                                                  @Nonnull @JsonProperty(CEDAR_TEMPLATE_IRI) String cedarTemplateIri,
                                                  @Nullable @JsonProperty(ALLOW_ADDITIONAL_COLUMNS) Boolean allowAdditionalColumns) {
    return new AutoValue_ValidateSpreadsheetRequest(spreadsheetData, cedarTemplateIri, allowAdditionalColumns);
  }

  @Nonnull
  @JsonProperty(SPREADSHEET_DATA)
  public abstract List<Map<String, Object>> getSpreadsheetData();

  @Nonnull
  @JsonProperty(CEDAR_TEMPLATE_IRI)
  public abstract String getCedarTemplateIri();

  @Nullable
  @JsonProperty(ALLOW_ADDITIONAL_COLUMNS)
  public abstract Boolean getAllowAdditionalColumns();
}
