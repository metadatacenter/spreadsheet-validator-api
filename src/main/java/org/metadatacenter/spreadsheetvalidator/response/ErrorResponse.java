package org.metadatacenter.spreadsheetvalidator.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ErrorResponse {

  private static final String MESSAGE = "message";
  private static final String CAUSE = "cause";
  private static final String STATUS_INFO = "statusInfo";
  private static final String FIX_SUGGESTION = "fixSuggestion";

  @Nonnull
  @JsonCreator
  public static ErrorResponse create(@Nonnull @JsonProperty(MESSAGE) String message,
                                     @Nonnull @JsonProperty(CAUSE) String cause,
                                     @Nonnull @JsonProperty(STATUS_INFO) String statusInfo,
                                     @Nullable @JsonProperty(FIX_SUGGESTION) String fixSuggestion) {
    return new AutoValue_ErrorResponse(message, cause, statusInfo, fixSuggestion);
  }

  public static ErrorResponse create(@Nonnull @JsonProperty(MESSAGE) String message,
                                     @Nonnull @JsonProperty(CAUSE) String cause,
                                     @Nonnull @JsonProperty(STATUS_INFO) String statusInfo) {
    return new AutoValue_ErrorResponse(message, cause, statusInfo, null);
  }

  @Nonnull
  @JsonProperty(MESSAGE)
  public abstract String getMessage();

  @Nonnull
  @JsonProperty(CAUSE)
  public abstract String getCause();

  @Nullable
  @JsonProperty(STATUS_INFO)
  public abstract String getStatusInfo();

  @Nullable
  @JsonProperty(FIX_SUGGESTION)
  public abstract String getFixSuggestion();
}
