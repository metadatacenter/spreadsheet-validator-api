package org.metadatacenter.spreadsheetvalidator.thirdparty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

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
  public String writeJsonString(Object o) throws JsonProcessingException {
    return objectMapper.writeValueAsString(o);
  }

  @Nonnull
  public <T> T writeObject(String jsonString, Class<T> objectType) throws JsonProcessingException {
    return objectMapper.readValue(jsonString, objectType);
  }

  @Nonnull
  public <T> T writeObject(JsonNode jsonNode, Class<T> objectType) throws JsonProcessingException {
    return objectMapper.treeToValue(jsonNode, objectType);
  }

  public void writeObjectToFile(Object o, File outputFile) throws IOException {
    var writer = objectMapper.writer(new DefaultPrettyPrinter());
    writer.writeValue(outputFile, o);
  }

  @Nonnull
  public Request createGetRequest(String uri) {
    return Request.Get(uri);
  }

  @Nonnull
  public Request createGetRequest(String uri, String apiKey) {
    return Request.Get(uri).addHeader("Authorization", apiKey);
  }

  @Nonnull
  public Request createPostRequest(String uri, String jsonPayload) {
    var request = Request.Post(uri).bodyString(jsonPayload, ContentType.APPLICATION_JSON);
    return request;
  }

  @Nonnull
  public Request createPostRequest(String uri, String apiKey, String jsonPayload) {
    var request = Request.Post(uri)
        .bodyString(jsonPayload, ContentType.APPLICATION_JSON)
        .addHeader("Authorization", apiKey);
    return request;
  }

  @Nonnull
  public HttpResponse execute(Request request) throws IOException {
    return request.execute().returnResponse();
  }

  @Nonnull
  public JsonNode processResponse(HttpResponse response) throws IOException {
    var responseEntity = response.getEntity();
    var jsonString = new String(EntityUtils.toByteArray(responseEntity));
    return parseJsonString(jsonString);
  }
}
