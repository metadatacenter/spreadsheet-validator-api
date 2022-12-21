package org.metadatacenter.spreadsheetvalidator;

import com.google.common.collect.ImmutableList;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ValidatorElementList {

  private final List<ValidatorElement> list = Lists.newArrayList();

  public void add(ValidatorElement validatorElement) {
    list.add(validatorElement);
  }

  public ImmutableList<ValidatorElement> getValidators() {
    return ImmutableList.copyOf(list);
  }

  public Stream<ValidatorElement> stream() {
    return list.stream();
  }
}
