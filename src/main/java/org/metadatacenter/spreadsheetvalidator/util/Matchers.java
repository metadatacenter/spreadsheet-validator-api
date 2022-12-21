package org.metadatacenter.spreadsheetvalidator.util;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Matchers {

  public static AssertionCriteria isString() {
    return value -> value instanceof String;
  }

  public static AssertionCriteria isString(String withRegexPattern) {
    return value -> ValueAssertion.equals(value, isString())
        && ((String) value).matches(withRegexPattern);
  }

  public static AssertionCriteria isDecimal() {
    return value -> value instanceof Number;
  }

  public static AssertionCriteria isInteger() {
    return value -> value instanceof Integer;
  }

  public static AssertionCriteria isNull() {
    return value -> value == null;
  }

  public static AssertionCriteria isEmpty() {
    return value -> ValueAssertion.equals(value, isString())
        && ((String) value).trim().isEmpty();
  }

  public static AssertionCriteria isNullOrEmpty() {
    return value -> ValueAssertion.equals(value, isNull())
        || ValueAssertion.equals(value, isEmpty());
  }
}
