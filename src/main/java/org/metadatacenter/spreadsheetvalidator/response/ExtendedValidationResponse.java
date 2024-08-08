package org.metadatacenter.spreadsheetvalidator.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.metadatacenter.spreadsheetvalidator.ValidationReport;
import org.metadatacenter.spreadsheetvalidator.domain.Spreadsheet;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetSchema;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ExtendedValidationResponse implements ValidationResponse {

  private static final String STATUS = "status";
  private static final String SCHEMA = "schema";
  private static final String DATA = "data";
  private static final String REPORTING = "reporting";

  @Nonnull
  @JsonCreator
  public static ExtendedValidationResponse create(@Nonnull @JsonProperty(STATUS) ValidationStatus status,
                                                  @Nonnull @JsonProperty(SCHEMA) SpreadsheetSchema schema,
                                                  @Nonnull @JsonProperty(DATA) Spreadsheet data,
                                                  @Nonnull @JsonProperty(REPORTING) ValidationReport reporting) {
    return new AutoValue_ExtendedValidationResponse(status, schema, data, reporting);
  }

  @Nonnull
  @JsonProperty(STATUS)
  public abstract ValidationStatus getStatus();

  @Nonnull
  @JsonProperty(SCHEMA)
  public abstract SpreadsheetSchema getSchema();

  @Nonnull
  @JsonProperty(DATA)
  public abstract Spreadsheet getData();

  @Nonnull
  @JsonProperty(REPORTING)
  public abstract ValidationReport getReporting();

  @Override
  @JsonIgnore
  public String getValidationStatus() {
    return getStatus().toString();
  }

  @Override
  @JsonIgnore
  public ValidationReport getValidationReport() {
    return getReporting();
  }
}
