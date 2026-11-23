package org.metadatacenter.spreadsheetvalidator.thirdparty;

import autovalue.shaded.com.google.common.collect.ImmutableList;
import autovalue.shaded.com.google.common.collect.ImmutableMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.spreadsheetvalidator.exception.ValidatorServiceException;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class TerminologyService {

  private final CedarConfig cedarConfig;

  private final RestServiceHandler restServiceHandler;

  @Inject
  public TerminologyService(CedarConfig cedarConfig,
                            RestServiceHandler restServiceHandler) {
    this.cedarConfig = checkNotNull(cedarConfig);
    this.restServiceHandler = checkNotNull(restServiceHandler);
  }

  /**
   * The largest page the terminology server's integrated search serves. A configured page size above
   * it is reduced to it, since the server refuses a larger limit rather than shortening the page.
   */
  static final int MAX_PAGE_SIZE = 500;

  /**
   * The furthest a value set is read. It bounds the walk should the server keep answering full pages.
   */
  static final int MAX_VALUES = 100_000;

  /**
   * Every value the constraints allow, read a page at a time by {@code limit} and {@code offset} until
   * a page comes back short or the server's {@code totalCount} is reached.
   */
  @Nonnull
  public ImmutableList<OntologyValue> getOntologyValues(@Nonnull String fieldName,
                                                        @Nonnull ValueConstraints valueConstraints) {
    var limit = Math.max(1, Math.min(cedarConfig.getPageSize(), MAX_PAGE_SIZE));
    var values = ImmutableList.<OntologyValue>builder();
    var offset = 0;
    while (offset < MAX_VALUES) {
      var page = getPage(fieldName, valueConstraints, offset, limit);
      var collection = page.withArray("collection");
      collection.elements().forEachRemaining(ontologyValue -> {
        try {
          values.add(restServiceHandler.writeObject(ontologyValue, OntologyValue.class));
        } catch (JsonProcessingException e) {
          throw badResponseError(e);
        }
      });
      offset += collection.size();
      var totalCount = page.path("totalCount");
      if (collection.size() < limit || (totalCount.isNumber() && offset >= totalCount.asInt())) {
        break;
      }
    }
    return values.build();
  }

  @Nonnull
  private JsonNode getPage(@Nonnull String fieldName, @Nonnull ValueConstraints valueConstraints,
                           int offset, int limit) {
    // Prepare the request
    Request request = null;
    String payloadString = "";
    try {
      var payload = ImmutableMap.of(
          "parameterObject", ImmutableMap.of("valueConstraints", valueConstraints),
          "limit", limit,
          "offset", offset);
      payloadString = restServiceHandler.writeJsonString(payload);
      request = restServiceHandler.createPostRequest(
          cedarConfig.getTerminologyEndpoint(),
          "apiKey " + cedarConfig.getApiKey(),
          payloadString);
    } catch (JsonProcessingException e) {
      throw badPayloadError(e);
    }

    // Execute the request
    var response = makeApiRequest(request, fieldName);

    // Process the response
    try {
      return restServiceHandler.processResponse(response);
    } catch (IOException e) {
      throw badResponseError(e);
    }
  }

  private HttpResponse makeApiRequest(Request request, String fieldName) {
    var attempt = 0;
    var maxRetries = cedarConfig.getMaxRetries();
    while (attempt < maxRetries) {
      try {
        var response = restServiceHandler.execute(request);
        var statusCode = response.getStatusLine().getStatusCode();

        // Handle HTTP 429 - Too many requests
        if (statusCode == 429) {
          attempt++;
          long sleepTime = cedarConfig.getBackoffSleepTime() * (1L << (attempt - 1)); // Exponential backoff
          System.out.println("429 received. Retrying after " + sleepTime + " ms...");
          Thread.sleep(sleepTime);
          continue;
        }

        if (statusCode == HttpStatus.SC_OK) {
          return response;
        } else {
          throw badRequestError(request, fieldName);
        }
      } catch (IOException | InterruptedException e) {
        if (attempt == maxRetries - 1) {
          throw failedAttemptError(fieldName, maxRetries);
        }
        attempt++;
      }
    }
    throw failedAttemptError(fieldName, attempt);
  }

  @Nonnull
  private static ValidatorServiceException badPayloadError(JsonProcessingException cause) {
    return new ValidatorServiceException("Failed to prepare the request payload.",
        cause, HttpStatus.SC_INTERNAL_SERVER_ERROR);
  }

  @Nonnull
  private static ValidatorServiceException badResponseError(IOException cause) {
    return new ValidatorServiceException("Failed to process response from CEDAR Terminology server.",
        cause, HttpStatus.SC_INTERNAL_SERVER_ERROR);
  }

  @Nonnull
  private static ValidatorServiceException badRequestError(Request request, String fieldName) {
    var responseMessage = String.format("Bad request to /bioportal/integrated-search.\n%s", request);
    var cause = new IOException(responseMessage);
    return new ValidatorServiceException(String.format(
        "Failed to populate categorical values for the '%s' field.", fieldName),
        cause, HttpStatus.SC_INTERNAL_SERVER_ERROR);
  }

  @Nonnull
  private static ValidatorServiceException failedAttemptError(String fieldName, int attempt) {
    var cause = new IOException("Failed after " + attempt + " attempts");
    return new ValidatorServiceException(String.format(
        "Failed to populate categorical values for the '%s' field.", fieldName),
        cause, HttpStatus.SC_INTERNAL_SERVER_ERROR);
  }
}
