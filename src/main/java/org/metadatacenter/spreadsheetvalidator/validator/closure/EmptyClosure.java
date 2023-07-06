package org.metadatacenter.spreadsheetvalidator.validator.closure;

import org.metadatacenter.spreadsheetvalidator.validator.closure.Closure;

import javax.annotation.Nullable;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class EmptyClosure implements Closure<Object> {
  @Override
  @Nullable
  public Object execute(Object... inputs) {
    return null;
  }
}
