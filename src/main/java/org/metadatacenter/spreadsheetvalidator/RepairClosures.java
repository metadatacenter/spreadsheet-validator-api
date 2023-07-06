package org.metadatacenter.spreadsheetvalidator;

import com.google.common.collect.Maps;
import org.metadatacenter.spreadsheetvalidator.validator.closure.Closure;
import org.metadatacenter.spreadsheetvalidator.validator.closure.EmptyClosure;

import javax.annotation.Nonnull;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class RepairClosures {

  private final Map<String, Closure> closures = Maps.newHashMap();

  public void add(@Nonnull String key, @Nonnull Closure closure) {
    checkNotNull(key);
    checkNotNull(closure);
    closures.put(key, closure);
  }

  @Nonnull
  public Closure get(String key) {
    checkNotNull(key);
    var closure = closures.get(key);
    return (closure == null) ? new EmptyClosure() : closure;
  }
}
