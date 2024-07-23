package org.metadatacenter.spreadsheetvalidator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ValidationResult {

  private final List<Map<String, Object>> itemList = Lists.<Map<String, Object>>newArrayList();

  public void add(Map<String, Object> item) {
    itemList.add(item);
  }

  public ImmutableList<Map<String, Object>> get() {
    return ImmutableList.copyOf(itemList);
  }

  public Stream<Map<String, Object>> stream() {
    return itemList.stream();
  }
}
