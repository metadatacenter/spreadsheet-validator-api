package org.metadatacenter.spreadsheetvalidator.validator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.metadatacenter.spreadsheetvalidator.ResultCollector;
import org.metadatacenter.spreadsheetvalidator.ValidationReport;
import org.metadatacenter.spreadsheetvalidator.ValidationResult;

import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ERROR_TYPE;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SUGGESTION;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.VALUE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ResultCollectorImpl implements ResultCollector {

  @Override
  public ValidationReport of(ValidationResult result) {
    return ValidationReport.create(result.stream()
        .map(validationItem -> {
          var map = Maps.<String, Object>newHashMap();
          map.put("row", validationItem.getRowNumber());
          map.put("column", validationItem.getColumnName());
          if (validationItem.hasProp(VALUE)) {
            map.put("value", validationItem.getProp(VALUE));
          }
          if (validationItem.hasProp(SUGGESTION)) {
            map.put("repairSuggestion", validationItem.getProp(SUGGESTION));
          }
          map.put("errorType", validationItem.getProp(ERROR_TYPE));
          return ImmutableMap.copyOf(map);
        })
        .collect(ImmutableList.toImmutableList()));
  }
}
