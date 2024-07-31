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
public abstract class MetaSchemaConfig {

  private static final String TARGET_IRI = "targetIri";

  @Nonnull
  @JsonCreator
  public static MetaSchemaConfig create(@Nonnull @JsonProperty(TARGET_IRI) String metaSchemaIri) {
    return new AutoValue_MetaSchemaConfig(metaSchemaIri);
  }

  @Nonnull
  @JsonProperty(TARGET_IRI)
  public abstract String getTargetIri();
}
