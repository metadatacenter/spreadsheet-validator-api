package org.metadatacenter.spreadsheetvalidator.algorithm;

import org.metadatacenter.spreadsheetvalidator.Closure;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AutoSuggester implements Closure<String> {
  @Override
  public String execute(Object... inputs) {
    var inputString = inputs[0];
    var dictionary = inputs[1];
    return null;
  }
}
