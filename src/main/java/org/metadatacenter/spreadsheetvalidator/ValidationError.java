package org.metadatacenter.spreadsheetvalidator;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.metadatacenter.spreadsheetvalidator.validator.ValueContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ValidationError {

  @Nonnull
  public static ValidationError create(@Nonnull Integer rowNumber,
                                       @Nonnull String columnName,
                                       @Nonnull String columnLabel,
                                       @Nonnull String errorDescription,
                                       @Nonnull ImmutableMap<String, Object> additionalProps) {
    return new AutoValue_ValidationError(rowNumber, columnName, columnLabel, errorDescription, additionalProps);
  }

  public static Builder builder(@Nonnull ValueContext valueContext) {
    var rowNumber = valueContext.getRow();
    var columnName = valueContext.getColumn();
    var columnLabel = valueContext.getColumnDescription().getColumnLabel();
    return new ValidationError.Builder(rowNumber, columnName, columnLabel);
  }

  @Nonnull
  public abstract Integer getRowNumber();

  @Nonnull
  public abstract String getColumnName();

  @Nonnull
  public abstract String getColumnLabel();

  @Nonnull
  public abstract String getErrorDescription();

  @Nonnull
  public abstract ImmutableMap<String, Object> getAdditionalProps();

  @Nullable
  public Object getProp(String name) {
    return getAdditionalProps().get(name);
  }

  public boolean hasProp(String name) {
    return getAdditionalProps().containsKey(name);
  }

  public static class Builder {

    private final Integer rowNumber;
    private final String columnName;
    private final String columnLabel;
    private String errorDescription;

    private Map<String, Object> additionalProps = Maps.newHashMap();

    public Builder(@Nonnull Integer rowNumber, @Nonnull String columnName, @Nonnull String columnLabel) {
      this.rowNumber = checkNotNull(rowNumber);
      this.columnName = checkNotNull(columnName);
      this.columnLabel = checkNotNull(columnLabel);
    }

    public Builder setErrorDescription(@Nonnull String description) {
      checkNotNull(description);
      this.errorDescription = description;
      return this;
    }

    public Builder setProp(String name, Object value) {
      if (value != null) {
        additionalProps.put(name, value);
      }
      return this;
    }

    public ValidationError build() {
      checkNotNull(errorDescription);
      return ValidationError.create(
          rowNumber,
          columnName,
          columnLabel,
          errorDescription,
          ImmutableMap.copyOf(additionalProps));
    }
  }
}
