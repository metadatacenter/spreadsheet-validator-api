package org.metadatacenter.spreadsheetvalidator.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public enum ValueType {

  STRING("string"),
  NUMBER("number"),
  DECIMAL("decimal"),
  INTEGER("integer"),
  URL("url");

  private final String canonicalName;

  ValueType(@Nonnull String canonicalName) {
    this.canonicalName = canonicalName;
  }

  @JsonValue
  public String getCanonicalName() {
    return canonicalName;
  }

  @Override
  public String toString() {
    return getCanonicalName();
  }
}
