package org.metadatacenter.spreadsheetvalidator.excel.model;

import com.google.auto.value.AutoValue;
import org.metadatacenter.spreadsheetvalidator.domain.ValueType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class BuiltinType {

  @Nonnull
  public static BuiltinType create(@Nonnull String name,
                                   @Nonnull ValueType type,
                                   @Nonnull ValueType subType,
                                   @Nullable String inputPattern,
                                   @Nullable String inputExample) {
    return new AutoValue_BuiltinType(name, type, subType, inputPattern, inputExample);
  }

  @Nonnull
  public abstract String getName();

  @Nonnull
  public abstract ValueType getType();

  @Nonnull
  public abstract ValueType getSubType();

  @Nullable
  public abstract String getInputPattern();

  @Nullable
  public abstract String getInputExample();
}
