package org.metadatacenter.spreadsheetvalidator.validator.closure;

import org.metadatacenter.spreadsheetvalidator.Closure;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NumberExtractor implements Closure<Number> {

  private final String numberOnlyPattern = "(\\d+(?:\\.\\d+)?)";
  private final Pattern  pattern = Pattern.compile(numberOnlyPattern, Pattern.MULTILINE);

  @Override
  @Nullable
  public Number execute(Object... inputs) {
    var string = String.valueOf(inputs[0]);
    var matcher = pattern.matcher(string);
    if (matcher.find()) {
      var firstMatch = matcher.group(1);
      if (firstMatch.contains(".")) {
        return Double.valueOf(firstMatch);
      } else {
        return Integer.valueOf(firstMatch);
      }
    }
    return null;
  }
}
