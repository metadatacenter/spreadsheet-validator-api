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
  private static final String REPO_BASE_URL = "repoBaseUrl";
  private static final String RESOURCE_BASE_URL = "resourceBaseUrl";
  private static final String TERMINOLOGY_ENDPOINT = "terminologyEndpoint";

  @Nonnull
  @JsonCreator
  public static CedarConfig create(@Nonnull @JsonProperty(API_KEY) String apiKey,
                                   @Nonnull @JsonProperty(REPO_BASE_URL) String repoBaseUrl,
                                   @Nonnull @JsonProperty(RESOURCE_BASE_URL) String resourceBaseUrl,
                                   @Nonnull @JsonProperty(TERMINOLOGY_ENDPOINT) String terminologyEndpoint) {
    return new AutoValue_CedarConfig(apiKey, repoBaseUrl, resourceBaseUrl, terminologyEndpoint);
  }

  @Nonnull
  @JsonProperty(API_KEY)
  public abstract String getApiKey();

  @Nonnull
  @JsonProperty(REPO_BASE_URL)
  public abstract String getRepoBaseUrl();

  @Nonnull
  @JsonProperty(RESOURCE_BASE_URL)
  public abstract String getResourceBaseUrl();

  @Nonnull
  @JsonProperty(TERMINOLOGY_ENDPOINT)
  public abstract String getTerminologyEndpoint();
}
