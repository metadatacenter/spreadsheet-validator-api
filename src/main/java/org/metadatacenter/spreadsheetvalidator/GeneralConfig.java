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

  @Nonnull
  @JsonCreator
  public static GeneralConfig create(@Nonnull @JsonProperty(ENCODING) String encoding) {
    return new AutoValue_GeneralConfig(encoding);
  }

  @Nonnull
  @JsonProperty(ENCODING)
  public abstract String getEncoding();
}
