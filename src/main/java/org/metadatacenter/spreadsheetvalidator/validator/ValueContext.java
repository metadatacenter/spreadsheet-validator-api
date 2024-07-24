package org.metadatacenter.spreadsheetvalidator.validator;

import com.google.auto.value.AutoValue;
import org.metadatacenter.spreadsheetvalidator.domain.ColumnDescription;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ValueContext {

  public static ValueContext create(@Nonnull Integer row,
                                    @Nonnull String column,
                                    @Nonnull ColumnDescription columnDescription) {
    return new AutoValue_ValueContext(row, column, columnDescription);
  }

  @Nonnull
  public abstract Integer getRow();

  @Nonnull
  public abstract String getColumn();

  @Nonnull
  public abstract ColumnDescription getColumnDescription();
}
