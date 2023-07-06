package org.metadatacenter.spreadsheetvalidator;

import autovalue.shaded.com.google.common.collect.ImmutableList;
import autovalue.shaded.com.google.common.collect.ImmutableMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Streams;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import org.metadatacenter.artifacts.model.core.ValueConstraints;
import org.metadatacenter.spreadsheetvalidator.validator.exception.BadConceptUriException;
import org.metadatacenter.spreadsheetvalidator.validator.exception.BadValidatorRequestException;
import org.metadatacenter.spreadsheetvalidator.validator.exception.RemoteAccessException;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
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
  public ImmutableList<OntologyValue> getOntologyValues(@Nonnull ValueConstraints valueConstraints) {
    // Prepare the request
    Request request = null;
    try {
      var payload =
          ImmutableMap.of(
              "parameterObject", ImmutableMap.of("valueConstraints", valueConstraints),
              "pageSize", 5000,
              "page", 1);
      request = restServiceHandler.createPostRequest(
          cedarConfig.getTerminologyEndpoint(),
          "apiKey " + cedarConfig.getApiKey(),
          payload);
    } catch (JsonProcessingException e) {
      throw new BadRequestException(e);
    }

    // Execute the request
    HttpResponse response = null;
    try {
      response = restServiceHandler.execute(request);
    } catch (IOException e) {
      throw new RemoteAccessException(""); // TODO: Revisit this
    }

    // Process the response
    try {
      var responseObject = processResponse(response);
      return Streams.stream(responseObject.withArray("collection").elements())
          .map(item -> {
            try {
              return restServiceHandler.writeObject(item, OntologyValue.class);
            } catch (JsonProcessingException e) {
              throw new RuntimeException(e); // TODO: Revisit this
            }
          })
          .collect(ImmutableList.toImmutableList());
    } catch (BadValidatorRequestException e) { // TODO: Revisit this
      throw e;
    }
  }

  private ObjectNode processResponse(HttpResponse response) {
    var statusLine = response.getStatusLine();
    switch (statusLine.getStatusCode()) {
      case HttpStatus.SC_OK:
        try {
          var jsonString = new String(EntityUtils.toByteArray(response.getEntity()));
          return (ObjectNode) restServiceHandler.parseJsonString(jsonString);
        } catch (IOException e) {
          throw new BadConceptUriException(e.getLocalizedMessage());
        }
      default:
        throw new RemoteAccessException(""); // TODO: Revisit this
    }
  }
}
