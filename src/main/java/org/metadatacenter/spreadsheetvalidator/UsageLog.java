package org.metadatacenter.spreadsheetvalidator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class UsageLog {

  private static final String SERVICE_API = "serviceApi";
  private static final String REQUEST_TIMESTAMP = "requestTimestamp";
  private static final String ORIGINATING_IP = "originatingIp";
  private static final String USER_AGENT = "userAgent";
  private static final String METADATA_SCHEMA_ID = "metadataSchemaId";
  private static final String STATUS_CODE = "statusCode";
  private static final String STATUS_MESSAGE = "statusMessage";
  private static final String VALIDATION_REPORT = "validationReport";

  @Nonnull
  @JsonCreator
  public static UsageLog create(@Nonnull @JsonProperty(SERVICE_API) String serviceApi,
                                @Nonnull @JsonProperty(REQUEST_TIMESTAMP) String requestTimestamp,
                                @Nonnull @JsonProperty(ORIGINATING_IP) String originatingIp,
                                @Nonnull @JsonProperty(USER_AGENT) String userAgent,
                                @Nonnull @JsonProperty(STATUS_CODE) int statusCode,
                                @Nonnull @JsonProperty(STATUS_MESSAGE) String statusMessage,
                                @Nullable @JsonProperty(METADATA_SCHEMA_ID) String metadataSchemaId,
                                @Nullable @JsonProperty(VALIDATION_REPORT) ValidationReport validationReport) {
    return new AutoValue_UsageLog(serviceApi, requestTimestamp, originatingIp, userAgent, statusCode,
        statusMessage, metadataSchemaId, validationReport);
  }

  @Nonnull
  @JsonProperty(SERVICE_API)
  public abstract String getServiceApi();

  @Nonnull
  @JsonProperty(REQUEST_TIMESTAMP)
  public abstract String getRequestTimestamp();

  @Nonnull
  @JsonProperty(ORIGINATING_IP)
  public abstract String getOriginatingIp();

  @Nonnull
  @JsonProperty(USER_AGENT)
  public abstract String getUserAgent();

  @Nonnull
  @JsonProperty(STATUS_CODE)
  public abstract int getStatusCode();

  @Nonnull
  @JsonProperty(STATUS_MESSAGE)
  public abstract String getStatusMessage();

  @Nullable
  @JsonProperty(METADATA_SCHEMA_ID)
  public abstract String getMetadataSchemaId();

  @Nullable
  @JsonProperty(VALIDATION_REPORT)
  public abstract ValidationReport getValidationReport();
}
