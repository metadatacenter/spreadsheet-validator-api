package org.metadatacenter.spreadsheetvalidator;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ErrorLocation {

  @Nonnull
  public static ErrorLocation create(@Nonnull String columnName,
                                     @Nonnull Integer rowIndex) {
    return new AutoValue_ErrorLocation(columnName, rowIndex);
  }

  @Nonnull
  public abstract String getColumnName();

  @Nonnull
  public abstract Integer getRowIndex();
}
