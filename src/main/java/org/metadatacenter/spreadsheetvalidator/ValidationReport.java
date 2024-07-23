package org.metadatacenter.spreadsheetvalidator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ValidationReport {

  @JsonCreator
  public static ValidationReport create(@Nonnull ImmutableList<ValidationReportItem> reportItems) {
    return new AutoValue_ValidationReport(reportItems);
  }

  @JsonValue
  public abstract ImmutableList<ValidationReportItem> getItems();

  public boolean isEmpty() {
    return getItems().isEmpty();
  }
}
