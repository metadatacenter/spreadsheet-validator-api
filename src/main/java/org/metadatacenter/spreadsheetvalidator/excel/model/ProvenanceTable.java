package org.metadatacenter.spreadsheetvalidator.excel.model;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ProvenanceTable {

  @Nonnull
  public static ProvenanceTable create(Map<String, Object> records,
                                       ProvenanceKeyword provenanceKeyword) {
    return new AutoValue_ProvenanceTable(records, provenanceKeyword);
  }

  @Nonnull
  protected abstract Map<String, Object> getRecords();

  @Nonnull
  protected abstract ProvenanceKeyword getProvenanceKeyword();

  @Nonnull
  public Optional<String> getSchemaName() {
    return Optional.ofNullable((String) getRecords().get(getProvenanceKeyword().ofSchemaName()));
  }

  @Nonnull
  public Optional<String> getSchemaVersion() {
    return Optional.ofNullable((String) getRecords().get(getProvenanceKeyword().ofSchemaVersion()));
  }

  @Nonnull
  public Optional<String> getCreationDate() {
    return Optional.ofNullable((String) getRecords().get(getProvenanceKeyword().ofCreationDate()));
  }

  @Nonnull
  public Optional<String> getAccessUrl() {
    return Optional.ofNullable((String) getRecords().get(getProvenanceKeyword().ofAccessUrl()));
  }
}
