package org.metadatacenter.spreadsheetvalidator.algorithm;

import org.apache.batik.css.engine.value.Value;
import org.apache.lucene.util.mutable.MutableValue;
import org.metadatacenter.spreadsheetvalidator.domain.ValueType;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AutoSuggester {

  private final ValueConverter valueConverter;

  @Inject
  public AutoSuggester(@Nonnull ValueConverter valueConverter) {
    this.valueConverter = checkNotNull(valueConverter);
  }

  public TargetValue replace(Object value) {
    return ;
  }
}
