package org.metadatacenter.spreadsheetvalidator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ExcelConfig {

  private static final String META_SCHEMA_IRI = "metaSchemaIri";

  @Nonnull
  @JsonCreator
  public static ExcelConfig create(@Nonnull @JsonProperty(META_SCHEMA_IRI) String metaSchemaIri) {
    return new AutoValue_ExcelConfig(metaSchemaIri);
  }

  @Nonnull
  @JsonProperty(META_SCHEMA_IRI)
  public abstract String getMetaSchemaIri();
}
