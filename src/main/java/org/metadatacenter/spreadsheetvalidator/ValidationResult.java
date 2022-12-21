package org.metadatacenter.spreadsheetvalidator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ValidationResult {

  private final List<ValidationError> errorList = Lists.newArrayList();

  public void add(ValidationError error) {
    errorList.add(error);
  }

  public ImmutableList<ValidationError> getList() {
    return ImmutableList.copyOf(errorList);
  }

  public Stream<ValidationError> stream() {
    return errorList.stream();
  }
}
