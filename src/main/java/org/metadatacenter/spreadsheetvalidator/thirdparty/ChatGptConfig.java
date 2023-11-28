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
public abstract class ChatGptConfig {

  private static final String BASE_URL = "baseUrl";
  private static final String API_KEY = "apiKey";
  private static final String MODEL = "model";

  @Nonnull
  @JsonCreator
  public static ChatGptConfig create(@Nonnull @JsonProperty(BASE_URL) String baseUrl,
                                     @Nonnull @JsonProperty(API_KEY) String apiKey,
                                     @Nonnull @JsonProperty(MODEL) String model) {
    return new AutoValue_ChatGptConfig(baseUrl, apiKey, model);
  }

  @Nonnull
  @JsonProperty(BASE_URL)
  public abstract String getBaseUrl();

  @Nonnull
  @JsonProperty(API_KEY)
  public abstract String getApiKey();

  @Nonnull
  @JsonProperty(MODEL)
  public abstract String getModel();
}
