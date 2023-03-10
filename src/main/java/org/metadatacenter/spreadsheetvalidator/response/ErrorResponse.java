package org.metadatacenter.spreadsheetvalidator.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ErrorResponse {

  private static final String CODE = "code";

  private static final String MESSAGE = "message";

  private static final String SUGGESTION = "suggestion";

  @Nonnull
  @JsonCreator
  public static ErrorResponse create(@Nonnull @JsonProperty(CODE) Integer errorCode,
                                     @Nonnull @JsonProperty(MESSAGE) String message,
                                     @Nonnull @JsonProperty(SUGGESTION) String suggestion) {
    return new AutoValue_ErrorResponse(errorCode, message, suggestion);
  }

  @Nonnull
  @JsonProperty(CODE)
  public abstract Integer getErrorCode();

  @Nonnull
  @JsonProperty(MESSAGE)
  public abstract String getMessage();

  @Nonnull
  @JsonProperty(SUGGESTION)
  public abstract String getSuggestion();
}
