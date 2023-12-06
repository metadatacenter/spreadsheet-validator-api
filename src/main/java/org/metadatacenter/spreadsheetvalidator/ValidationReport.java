package org.metadatacenter.spreadsheetvalidator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ValidationReport {

  @JsonCreator
  public static ValidationReport create(@Nonnull ImmutableList<Map<String, Object>> reportItems) {
    return new AutoValue_ValidationReport(reportItems);
  }

  @JsonIgnore
  public static ValidationReport.Builder builder() {
    return new ValidationReport.Builder();
  }

  @JsonValue
  public abstract ImmutableList<Map<String, Object>> getItems();

  public boolean isEmpty() {
    return getItems().isEmpty();
  }

  public static class Builder {

    private final List<Map<String, Object>> reportItems = Lists.newArrayList();

    public Builder add(ImmutableMap<String, Object> reportItem) {
      reportItems.add(reportItem);
      return this;
    }

    public ValidationReport build() {
      return ValidationReport.create(ImmutableList.copyOf(reportItems));
    }
  }
}
