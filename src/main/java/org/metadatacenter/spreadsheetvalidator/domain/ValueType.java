package org.metadatacenter.spreadsheetvalidator.domain;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public enum ValueType {

  STRING("string"),
  NUMBER("number"),
  DECIMAL("decimal"),
  INTEGER("integer");

  private final String canonicalName;

  ValueType(@Nonnull String canonicalName) {
    this.canonicalName = canonicalName;
  }

  public String getCanonicalName() {
    return canonicalName;
  }

  @Override
  public String toString() {
    return getCanonicalName();
  }
}
