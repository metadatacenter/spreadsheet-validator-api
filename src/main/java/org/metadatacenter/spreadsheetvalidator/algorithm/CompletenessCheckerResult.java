package org.metadatacenter.spreadsheetvalidator.algorithm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class CompletenessCheckerResult {

  @Nonnull
  @JsonCreator
  public static CompletenessCheckerResult create(@Nonnull Integer rowNumber,
                                                 @Nonnull String columnName,
                                                 @Nonnull ErrorType errorType) {
    return new AutoValue_CompletenessCheckerResult(rowNumber, columnName, errorType);
  }

  @Nonnull
  public abstract Integer getRowNumber();

  @Nonnull
  public abstract String getColumnName();

  @Nonnull
  public abstract ErrorType getErrorType();
}
