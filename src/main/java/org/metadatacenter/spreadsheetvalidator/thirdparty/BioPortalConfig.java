package org.metadatacenter.spreadsheetvalidator.thirdparty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.metadatacenter.spreadsheetvalidator.AutoValue_BioPortalConfig;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class BioPortalConfig {

  private static final String API_KEY = "apiKey";
  private static final String BASE_URL = "baseUrl";

  @Nonnull
  @JsonCreator
  public static BioPortalConfig create(@Nonnull @JsonProperty(API_KEY) String apiKey,
                                       @Nonnull @JsonProperty(BASE_URL) String baseUrl) {
    return new AutoValue_BioPortalConfig(apiKey, baseUrl);
  }

  @Nonnull
  @JsonProperty(API_KEY)
  public abstract String getApiKey();

  @Nonnull
  @JsonProperty(BASE_URL)
  public abstract String getBaseUrl();
}
