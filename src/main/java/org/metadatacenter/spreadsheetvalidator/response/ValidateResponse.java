package org.metadatacenter.spreadsheetvalidator.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.metadatacenter.spreadsheetvalidator.ValidationReport;
import org.metadatacenter.spreadsheetvalidator.domain.Spreadsheet;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetSchema;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ValidateResponse {

  private static final String SCHEMA = "schema";
  private static final String DATA = "data";
  private static final String REPORTING = "reporting";

  @Nonnull
  @JsonCreator
  public static ValidateResponse create(@Nonnull @JsonProperty(SCHEMA) SpreadsheetSchema schema,
                                        @Nonnull @JsonProperty(DATA) Spreadsheet data,
                                        @Nonnull @JsonProperty(REPORTING) ValidationReport reporting) {
    return new AutoValue_ValidateResponse(schema, data, reporting);
  }

  @Nonnull
  @JsonProperty(SCHEMA)
  public abstract SpreadsheetSchema getSchema();

  @Nonnull
  @JsonProperty(DATA)
  public abstract Spreadsheet getData();

  @Nonnull
  @JsonProperty(REPORTING)
  public abstract ValidationReport getReporting();
}
