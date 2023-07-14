package org.metadatacenter.spreadsheetvalidator.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class UrlCheckResponse {

  private static final String TARGET_URL = "targetUrl";
  private static final String IS_REACHABLE = "isReachable";

  @JsonCreator
  public static UrlCheckResponse create(@Nonnull @JsonProperty(TARGET_URL) String targetUrl,
                                        @Nonnull @JsonProperty(IS_REACHABLE) Boolean isReachable) {
    return new AutoValue_UrlCheckResponse(targetUrl, isReachable);
  }

  @Nonnull
  @JsonProperty(TARGET_URL)
  public abstract String getTargetUrl();

  @Nonnull
  @JsonProperty(IS_REACHABLE)
  public abstract Boolean isReachable();
}
