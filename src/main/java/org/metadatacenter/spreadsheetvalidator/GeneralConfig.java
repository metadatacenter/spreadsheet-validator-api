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
public abstract class GeneralConfig {

  private static final String ENCODING = "encoding";
  private static final String SCHEMA_COLUMN = "schemaColumn";

  @Nonnull
  @JsonCreator
  public static GeneralConfig create(@Nonnull @JsonProperty(ENCODING) String encoding,
                                     @Nonnull @JsonProperty(SCHEMA_COLUMN) String schemaColumn) {
    return new AutoValue_GeneralConfig(encoding, schemaColumn);
  }

  @Nonnull
  @JsonProperty(ENCODING)
  public abstract String getEncoding();

  @Nonnull
  @JsonProperty(SCHEMA_COLUMN)
  public abstract String getSchemaColumn();
}
