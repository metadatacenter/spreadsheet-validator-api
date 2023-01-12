package org.metadatacenter.spreadsheetvalidator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class RestServiceHandler {

  private final ObjectMapper objectMapper;

  @Inject
  public RestServiceHandler(@Nonnull ObjectMapper objectMapper) {
    this.objectMapper = checkNotNull(objectMapper);
  }

  @Nonnull
  public JsonNode parseJsonString(String s) throws JsonProcessingException {
    return objectMapper.readTree(s);
  }

  @Nonnull
  public String writeJsonNode(JsonNode json) throws JsonProcessingException {
    return objectMapper.writeValueAsString(json);
  }

  @Nonnull
  public Request createGetRequest(String uri) {
    return createGetRequest(uri, Optional.empty());
  }

  @Nonnull
  public Request createGetRequest(String uri, String apiKey) {
    return createGetRequest(uri, Optional.of(apiKey));
  }

  @Nonnull
  public Request createGetRequest(String uri, Optional<String> apiKey) {
    var request = Request.Get(uri);
    request = addAuthorization(apiKey, request);
    return request;
  }

  @Nonnull
  public Request createPostRequest(String uri, JsonNode payload)
      throws JsonProcessingException {
    return createPostRequest(uri, payload, Optional.empty());
  }

  @Nonnull
  public Request createPostRequest(String uri, JsonNode payload, String apiKey)
      throws JsonProcessingException {
    return createPostRequest(uri, payload, Optional.of(apiKey));
  }

  @Nonnull
  public Request createPostRequest(String uri, JsonNode payload, Optional<String> apiKey)
      throws JsonProcessingException {
    var payloadString = writeJsonNode(payload);
    var request = Request.Post(uri).bodyString(payloadString, ContentType.APPLICATION_JSON);
    request = addAuthorization(apiKey, request);
    return request;
  }

  private Request addAuthorization(Optional<String> apiKey, Request request) {
    if (apiKey.isPresent()) {
      request = request.addHeader("Authorization", apiKey.get());
    }
    return request;
  }

  @Nonnull
  public HttpResponse execute(Request request) throws IOException {
    return request.execute().returnResponse();
  }
}
