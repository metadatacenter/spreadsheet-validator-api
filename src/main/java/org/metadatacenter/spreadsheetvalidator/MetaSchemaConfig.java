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

  private static final String ALLOW_CUSTOM_SCHEMA = "allowCustomSchema";

  @Nonnull
  @JsonCreator
  public static MetaSchemaConfig create(@Nonnull @JsonProperty(TARGET_IRI) String metaSchemaIri,
                                        @Nonnull @JsonProperty(ALLOW_CUSTOM_SCHEMA) boolean allowCustomSchema) {
    return new AutoValue_MetaSchemaConfig(metaSchemaIri, allowCustomSchema);
  }

  @Nonnull
  @JsonProperty(TARGET_IRI)
  public abstract String getTargetIri();

  @Nonnull
  @JsonProperty(ALLOW_CUSTOM_SCHEMA)
  public abstract boolean getAllowCustomSchemaFlag();
}
