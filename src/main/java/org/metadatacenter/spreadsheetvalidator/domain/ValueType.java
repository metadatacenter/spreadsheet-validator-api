package org.metadatacenter.spreadsheetvalidator.domain;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public enum ValueType {

  STRING("string", String.class),
  DECIMAL("decimal", Number.class),
  INTEGER("integer", Number.class);

  private final String canonicalName;

  private final Class representationClass;

  ValueType(@Nonnull String canonicalName,
            @Nonnull Class representationClass) {
    this.canonicalName = canonicalName;
    this.representationClass = representationClass;
  }

  public String getCanonicalName() {
    return canonicalName;
  }

  public Class asClass() {
    return representationClass;
  }

  @Override
  public String toString() {
    return getCanonicalName();
  }
}
