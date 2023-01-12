package org.metadatacenter.spreadsheetvalidator;

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
