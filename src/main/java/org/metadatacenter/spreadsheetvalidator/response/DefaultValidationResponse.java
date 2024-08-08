package org.metadatacenter.spreadsheetvalidator.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.metadatacenter.spreadsheetvalidator.ValidationReport;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class DefaultValidationResponse implements ValidationResponse {

  private static final String STATUS = "status";
  private static final String REPORTING = "reporting";

  @Nonnull
  @JsonCreator
  public static DefaultValidationResponse create(@Nonnull @JsonProperty(STATUS) ValidationStatus status,
                                                 @Nonnull @JsonProperty(REPORTING) ValidationReport report) {
    return new AutoValue_DefaultValidationResponse(status, report);
  }

  @Nonnull
  @JsonProperty(STATUS)
  public abstract ValidationStatus getStatus();

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
