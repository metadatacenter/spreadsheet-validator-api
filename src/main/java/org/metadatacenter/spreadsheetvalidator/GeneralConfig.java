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

  private static final String MAX_TABLE_SIZE = "maxTableSize";

  @Nonnull
  @JsonCreator
  public static GeneralConfig create(@Nonnull @JsonProperty(ENCODING) String encoding,
                                     @Nonnull @JsonProperty(SCHEMA_COLUMN) String schemaColumn,
                                     @Nonnull @JsonProperty(MAX_TABLE_SIZE) int maxTableSize) {
    return new AutoValue_GeneralConfig(encoding, schemaColumn, maxTableSize);
  }

  @Nonnull
  @JsonProperty(ENCODING)
  public abstract String getEncoding();

  @Nonnull
  @JsonProperty(SCHEMA_COLUMN)
  public abstract String getSchemaColumn();

  @Nonnull
  @JsonProperty(MAX_TABLE_SIZE)
  public abstract int getMaxTableSize();
}
