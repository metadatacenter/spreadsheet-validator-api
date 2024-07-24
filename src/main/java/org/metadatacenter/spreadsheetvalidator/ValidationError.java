package org.metadatacenter.spreadsheetvalidator;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.checkerframework.checker.units.qual.N;

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
  public static ValidationError create(@Nonnull String errorType,
                                       @Nonnull String errorMessage,
                                       @Nonnull ErrorLocation errorLocation,
                                       @Nonnull Map<String, Object> otherProps) {
    return new AutoValue_ValidationError(errorType, errorMessage, errorLocation, otherProps);
  }

  @Nonnull
  public static Builder builder() {
    return new Builder();
  }

  @Nonnull
  public abstract String getErrorType();

  @Nonnull
  public abstract String getErrorMessage();

  @Nonnull
  public abstract ErrorLocation getErrorLocation();

  @Nonnull
  public abstract Map<String, Object> getOtherProps();

  @Nullable
  public Object getOtherProp(String key) {
    return getOtherProps().get(key);
  }

  public static class Builder {

    private String errorType;
    private String errorMessage;
    private ErrorLocation errorLocation;
    private final Map<String, Object> otherProps = Maps.<String, Object>newHashMap();

    public Builder setErrorType(@Nonnull String errorType) {
      this.errorType = checkNotNull(errorType);
      return this;
    }

    public Builder setErrorMessage(@Nonnull String errorMessage) {
      this.errorMessage = checkNotNull(errorMessage);
      return this;
    }

    public Builder setErrorLocation(@Nonnull String columnName, @Nonnull Integer rowIndex) {
      this.errorLocation = ErrorLocation.create(columnName, rowIndex);
      return this;
    }

    public Builder setOtherProp(@Nonnull String key, @Nullable Object value) {
      if (value != null) {
        otherProps.put(key, value);
      }
      return this;
    }

    public ValidationError build() {
      return create(errorType, errorMessage, errorLocation, ImmutableMap.copyOf(otherProps));
    }
  }
}
