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
public abstract class ValidateResponse {

  private static final String ROW = "row";
  private static final String COLUMN = "column";
  private static final String VALUE = "value";
  private static final String SUGGESTION = "suggestion";
  private static final String ERROR_TYPE = "errorType";

  @Nonnull
  @JsonCreator
  public static ValidateResponse create(@Nonnull @JsonProperty(ROW) Integer row,
                                        @Nonnull @JsonProperty(COLUMN) String column,
                                        @Nullable @JsonProperty(VALUE) Object value,
                                        @Nullable @JsonProperty(SUGGESTION) Object suggestion,
                                        @Nonnull @JsonProperty(ERROR_TYPE) String errorType) {
    return new AutoValue_ValidateResponse(row, column, value, suggestion, errorType);
  }

  @Nonnull
  @JsonProperty(ROW)
  public abstract Integer getRow();

  @Nonnull
  @JsonProperty(COLUMN)
  public abstract String getColumn();

  @Nullable
  @JsonProperty(VALUE)
  public abstract Object getValue();

  @Nullable
  @JsonProperty(SUGGESTION)
  public abstract Object getSuggestion();

  @Nonnull
  @JsonProperty(ERROR_TYPE)
  public abstract String getErrorType();
}
