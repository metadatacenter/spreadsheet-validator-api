package org.metadatacenter.spreadsheetvalidator.validator.closure;

import com.google.common.collect.Lists;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.metadatacenter.spreadsheetvalidator.util.StringUtil;

import java.util.List;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class SimpleSimilarityChecker implements Closure<String> {

  @Override
  public String execute(Object... inputs) {
    var userInput = String.valueOf(inputs[0]);
    var permissibleValues = (List<String>) inputs[1];
    var maxDistance = 0;
    var outputList = Lists.<String>newArrayList();
    for (var permissibleValue : permissibleValues) {
      var distance = FuzzySearch.ratio(
          StringUtil.basicNormalization(userInput),
          StringUtil.basicNormalization(permissibleValue));
      if (distance > maxDistance) {
        outputList.add(0, permissibleValue);
        maxDistance = distance;
      } else {
        outputList.add(permissibleValue);
      }
    }
    return outputList.get(0);
  }
}
