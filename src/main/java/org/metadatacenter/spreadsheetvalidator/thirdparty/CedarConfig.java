package org.metadatacenter.spreadsheetvalidator.thirdparty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class CedarConfig {

  private static final String API_KEY = "apiKey";
  private static final String BASE_URL = "baseUrl";
  private static final String TERMINOLOGY_ENDPOINT = "terminologyEndpoint";

  @Nonnull
  @JsonCreator
  public static CedarConfig create(@Nonnull @JsonProperty(API_KEY) String apiKey,
                                   @Nonnull @JsonProperty(BASE_URL) String baseUrl,
                                   @Nonnull @JsonProperty(TERMINOLOGY_ENDPOINT) String terminologyEndpoint) {
    return new AutoValue_CedarConfig(apiKey, baseUrl, terminologyEndpoint);
  }

  @Nonnull
  @JsonProperty(API_KEY)
  public abstract String getApiKey();

  @Nonnull
  @JsonProperty(BASE_URL)
  public abstract String getBaseUrl();

  @Nonnull
  @JsonProperty(TERMINOLOGY_ENDPOINT)
  public abstract String getTerminologyEndpoint();
}
