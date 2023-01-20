package org.metadatacenter.spreadsheetvalidator;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

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
                                       @Nullable Object invalidValue,
                                       @Nonnull String errorDescription,
                                       @Nonnull ImmutableMap<String, Object> otherProps) {
    return new AutoValue_ValidationError(rowNumber, columnName, invalidValue, errorDescription, otherProps);
  }

  public static Builder builder() {
    return new ValidationError.Builder();
  }

  @Nonnull
  public abstract Integer getRowNumber();

  @Nonnull
  public abstract String getColumnName();

  @Nullable
  public abstract Object getInvalidValue();

  @Nonnull
  public abstract String getErrorDescription();

  @Nonnull
  public abstract ImmutableMap<String, Object> getOtherProps();

  @Nonnull
  public Object getOtherProp(String key) {
    return getOtherProps().get(key);
  }

  public static class Builder {

    private Integer rowNumber;
    private String columnName;
    private Object invalidValue;
    private String errorDescription;

    private Map<String, Object> otherProps = Maps.newHashMap();

    public Builder setRowNumber(@Nonnull Integer rowNumber) {
      checkNotNull(rowNumber);
      this.rowNumber = rowNumber;
      return this;
    }

    public Builder setColumnName(@Nonnull String columnName) {
      checkNotNull(columnName);
      this.columnName = columnName;
      return this;
    }

    public Builder setInvalidValue(@Nullable Object value) {
      this.invalidValue = value;
      return this;
    }

    public Builder setErrorDescription(@Nonnull String description) {
      checkNotNull(description);
      this.errorDescription = description;
      return this;
    }

    public Builder setOtherProp(String key, Object value) {
      if (value != null) {
        otherProps.put(key, value);
      }
      return this;
    }

    public ValidationError build() {
      checkNotNull(rowNumber);
      checkNotNull(columnName);
      checkNotNull(errorDescription);
      return ValidationError.create(rowNumber,
          columnName,
          invalidValue,
          errorDescription,
          ImmutableMap.copyOf(otherProps));
    }
  }
}
