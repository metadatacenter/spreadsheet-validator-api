package org.metadatacenter.spreadsheetvalidator.util;

import com.google.auto.value.AutoValue;
import org.metadatacenter.spreadsheetvalidator.domain.ValueType;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ValueAssertion {

  public static ValueAssertion of(@Nonnull Object value) {
    return new AutoValue_ValueAssertion(value);
  }

  @Nonnull
  public abstract Object getValue();

  public boolean isString() {
    return getValue() instanceof String;
  }

  public boolean isString(String regexPattern) {
    return isString() && ((String) getValue()).matches(regexPattern);
  }

  public boolean isDecimal() {
    return getValue() instanceof Number;
  }

  public boolean isInteger() {
    return getValue() instanceof Integer;
  }

  public boolean isNull() {
    return getValue() == null;
  }

  public boolean isNullOrEmpty() {
    return (isNull() || isString()) && ((String) getValue()).trim().isEmpty();
  }
}
