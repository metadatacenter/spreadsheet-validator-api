package org.metadatacenter.spreadsheetvalidator.thirdparty;

import autovalue.shaded.com.google.common.collect.ImmutableList;
import autovalue.shaded.com.google.common.collect.ImmutableMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Streams;
import jakarta.ws.rs.core.Response;
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

  @Nonnull
  public ImmutableList<OntologyValue> getOntologyValues(@Nonnull String fieldName,
                                                        @Nonnull ValueConstraints valueConstraints) {
    // Prepare the request
    Request request = null;
    String payloadString = "";
    try {
      var payload = ImmutableMap.of(
          "parameterObject", ImmutableMap.of("valueConstraints", valueConstraints),
          "pageSize", 4999,
          "page", 1);
      payloadString = restServiceHandler.writeJsonString(payload);
      request = restServiceHandler.createPostRequest(
          cedarConfig.getTerminologyEndpoint(),
          "apiKey " + cedarConfig.getApiKey(),
          payloadString);
    } catch (JsonProcessingException e) {
      throw new ValidatorServiceException("Failed to prepare the request payload.",
          e, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    // Execute the request
    HttpResponse response = null;
    try {
      response = restServiceHandler.execute(request);
      var statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != HttpStatus.SC_OK) {
        var responseMessage = String.format("Bad request to /bioportal/integrated-search.\n%s\n%s",
            request, payloadString);
        var cause = new IOException(responseMessage);
        throw new ValidatorServiceException(String.format(
            "Failed to populate categorical values for the '%s' field.", fieldName),
            cause, response.getStatusLine().getStatusCode());
      }
    } catch (IOException e) {
      throw new ValidatorServiceException("Error while connecting to CEDAR Terminology server.",
          e, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    // Process the response
    try {
      var responseObject = restServiceHandler.processResponse(response);
      return Streams.stream(responseObject.withArray("collection").elements())
          .map(ontologyValue -> {
            try {
              return restServiceHandler.writeObject(ontologyValue, OntologyValue.class);
            } catch (JsonProcessingException e) {
              throw new ValidatorServiceException("Failed to process response from CEDAR Terminology server.",
                  e, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
            }
          })
          .collect(ImmutableList.toImmutableList());
    } catch (IOException e) {
      throw new ValidatorServiceException("Failed to process response from CEDAR Terminology server.",
          e, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }
  }
}
