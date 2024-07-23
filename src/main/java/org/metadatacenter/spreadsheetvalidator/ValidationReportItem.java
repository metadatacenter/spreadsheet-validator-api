package org.metadatacenter.spreadsheetvalidator;

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
public abstract class ValidationReportItem {

  private static final String RECORD_NUMBER = "recordNumber";
  private static final String COLUMN_LABEL = "columnLabel";
  private static final String VARIABLE = "variable";
  private static final String VALUE = "value";
  private static final String ERROR_TYPE = "errorType";
  private static final String ERROR_MESSAGE = "errorMessage";
  private static final String REPAIR_SUGGESTION = "repairSuggestion";
  private static final String ROW = "row";
  private static final String COLUMN = "column";

  @Nonnull
  @JsonCreator
  public static ValidationReportItem create(@Nonnull @JsonProperty(RECORD_NUMBER) Integer recordNumber,
                                            @Nonnull @JsonProperty(COLUMN_LABEL) String columnLabel,
                                            @Nonnull @JsonProperty(VARIABLE) String variable,
                                            @Nullable @JsonProperty(VALUE) Object value,
                                            @Nonnull @JsonProperty(ERROR_TYPE) String errorType,
                                            @Nonnull @JsonProperty(ERROR_MESSAGE) String errorMessage,
                                            @Nullable @JsonProperty(REPAIR_SUGGESTION) String repairSuggestion,
                                            @Nonnull @JsonProperty(ROW) Integer rowIndex,
                                            @Nonnull @JsonProperty(COLUMN) String columnName) {
    return new AutoValue_ValidationReportItem(recordNumber, columnLabel, variable, value, errorType, errorMessage,
        repairSuggestion, rowIndex, columnName);
  }

  @Nonnull
  @JsonProperty(RECORD_NUMBER)
  public abstract Integer getRecordNumber();

  @Nonnull
  @JsonProperty(COLUMN_LABEL)
  public abstract String getColumnLabel();

  @Nonnull
  @JsonProperty(VARIABLE)
  public abstract String getVariable();

  @Nullable
  @JsonProperty(VALUE)
  public abstract Object getValue();

  @Nonnull
  @JsonProperty(ERROR_TYPE)
  public abstract String getErrorType();

  @Nonnull
  @JsonProperty(ERROR_MESSAGE)
  public abstract String getErrorMessage();

  @Nullable
  @JsonProperty(REPAIR_SUGGESTION)
  public abstract String getRepairSuggestion();

  @Nonnull
  @JsonProperty(ROW)
  public abstract Integer getRowIndex();

  @Nonnull
  @JsonProperty(COLUMN)
  public abstract String getColumnName();
}
