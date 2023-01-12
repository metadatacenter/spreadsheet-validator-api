package org.metadatacenter.spreadsheetvalidator.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class Spreadsheet {

  @JsonCreator
  public static Spreadsheet create(ImmutableList<SpreadsheetRow> spreadsheetRows) {
    return new AutoValue_Spreadsheet(spreadsheetRows);
  }

  public abstract ImmutableList<SpreadsheetRow> getRows();

  @JsonIgnore
  public int size() {
    return getRows().size();
  }

  @JsonIgnore
  public SpreadsheetRow get(int rowNumber) {
    return getRows().get(rowNumber);
  }

  @JsonIgnore
  public Stream<SpreadsheetRow> getRowStream() {
    return getRows().stream();
  }
}
