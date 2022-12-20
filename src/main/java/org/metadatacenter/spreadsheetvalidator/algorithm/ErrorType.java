package org.metadatacenter.spreadsheetvalidator.algorithm;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public enum ErrorType {

  MISSING_REQUIRED("missingRequired"),
  NOT_STANDARD_TERM("notStandardTerm"),
  NOT_NUMBER_TYPE("notNumberType"),
  NOT_STRING_TYPE("notStringType");

  private final String canonicalName;

  ErrorType(@Nonnull String canonicalName) {
    this.canonicalName = canonicalName;
  }

  public String getCanonicalName() {
    return this.canonicalName;
  }

  @Override
  public String toString() {
    return getCanonicalName();
  }
}
