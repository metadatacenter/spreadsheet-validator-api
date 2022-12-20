package org.metadatacenter.spreadsheetvalidator.algorithm;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class AdherenceCheckerResult {

  @Nonnull
  public static AdherenceCheckerResult create(@Nonnull Integer rowNumber,
                                              @Nonnull String columnName,
                                              @Nonnull Object actualValue,
                                              @Nullable Object suggestedValue,
                                              @Nonnull ErrorType errorType) {
    return new AutoValue_AdherenceCheckerResult(rowNumber, columnName, actualValue, suggestedValue, errorType);
  }

  @Nonnull
  public abstract Integer getRowNumber();

  @Nonnull
  public abstract String getColumnName();

  @Nonnull
  public abstract Object getActualValue();

  @Nullable
  public abstract Object getSuggestedValue();

  @Nonnull
  public abstract ErrorType getErrorType();
}
