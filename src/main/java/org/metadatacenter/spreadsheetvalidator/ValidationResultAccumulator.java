package org.metadatacenter.spreadsheetvalidator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ValidationResultAccumulator {

  private final List<Map<String, Object>> collections = Lists.<Map<String, Object>>newArrayList();

  public ValidationResultAccumulator add(Map<String, Object> item) {
    collections.add(item);
    return this;
  }

  public ValidationResult toValidationResult() {
    var resultList = ImmutableList.copyOf(collections);
    return ValidationResult.create(resultList);
  }
}
