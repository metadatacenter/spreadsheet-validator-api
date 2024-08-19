package org.metadatacenter.spreadsheetvalidator.util;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Matchers {

  public static AssertionCriteria not(@Nonnull AssertionCriteria criteria) {
    checkNotNull(criteria);
    return value -> !(criteria.evaluate(value));
  }

  public static AssertionCriteria isString() {
    return value -> value instanceof String;
  }

  public static AssertionCriteria isString(String withRegexPattern) {
    return value -> Assert.that(value, isString())
        && ((String) value).matches(withRegexPattern);
  }

  public static AssertionCriteria isNumber() {
    return value -> value instanceof Number;
  }

  public static AssertionCriteria isNull() {
    return Objects::isNull;
  }

  public static AssertionCriteria isEmpty() {
    return value -> Assert.that(value, isString())
        && ((String) value).trim().isEmpty();
  }

  public static AssertionCriteria isNullOrEmpty() {
    return value -> Assert.that(value, isNull())
        || Assert.that(value, isEmpty());
  }

  public static AssertionCriteria isIgnoreCaseMemberOf(List<String> strings) {
    return value -> strings.stream().anyMatch(((String) value)::equalsIgnoreCase);
  }
}
