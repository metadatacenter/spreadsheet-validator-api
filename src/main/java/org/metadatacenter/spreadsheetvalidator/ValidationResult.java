package org.metadatacenter.spreadsheetvalidator;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ValidationResult {

  @Nonnull
  public static ValidationResult create(@Nonnull List<Map<String, Object>> resultList) {
    return new AutoValue_ValidationResult(resultList);
  }

  @Nonnull
  public abstract List<Map<String, Object>> asList();

  @Nonnull
  public Stream<Map<String, Object>> stream() {
    return asList().stream();
  }
}
