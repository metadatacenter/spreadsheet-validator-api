package org.metadatacenter.spreadsheetvalidator.excel;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class MetadataSpreadsheet {

  @Nonnull
  public static MetadataSpreadsheet create(@Nonnull DataSheet dataSheet,
                                           @Nullable ProvenanceSheet provenanceSheet) {
    return new AutoValue_MetadataSpreadsheet(dataSheet, provenanceSheet);
  }

  @Nonnull
  public static MetadataSpreadsheet create(@Nonnull DataSheet dataSheet) {
    return create(dataSheet, null);
  }

  @Nonnull
  public abstract DataSheet getDataSheet();

  @Nullable
  public abstract ProvenanceSheet getProvenanceSheet();

  public boolean containsProvenanceSheet() {
    return getProvenanceSheet() != null;
  }
}
