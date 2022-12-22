package org.metadatacenter.spreadsheetvalidator.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Assert {

  public static boolean that(@Nullable Object value, @Nonnull AssertionCriteria criteria) {
    checkNotNull(criteria);
    return criteria.evaluate(value);
  }
}
