package org.metadatacenter.spreadsheetvalidator.thirdparty;

import autovalue.shaded.com.google.common.collect.ImmutableMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ChatGptService {

  private final ChatGptConfig chatGptConfig;

  private final RestServiceHandler restServiceHandler;

  @Inject
  public ChatGptService(@Nonnull ChatGptConfig chatGptConfig,
                        @Nonnull RestServiceHandler restServiceHandler) {
    this.chatGptConfig = checkNotNull(chatGptConfig);
    this.restServiceHandler = checkNotNull(restServiceHandler);
  }

  public JsonNode getResponse(@Nonnull String prompt) throws ServiceNotAvailable {
    Request request;
    String payloadString = "";
    try {
      var payload = ImmutableMap.of(
          "model", chatGptConfig.getModel(),
          "messages", ImmutableList.of(
              ImmutableMap.of(
                  "role", "system",
                  "content", "You are a metadata spelling corrector. Given a metadata " +
                      "field name and list of permissible field values for HuBMAP, your task " +
                      "is to identify the best match for a specified user input."),
              ImmutableMap.of(
                  "role", "user",
                  "content", prompt))
      );
      payloadString = restServiceHandler.writeJsonString(payload);
      request = restServiceHandler.createPostRequest(chatGptConfig.getBaseUrl(),
          "Bearer " + chatGptConfig.getApiKey(),
          payloadString);
    } catch (JsonProcessingException e) {
      throw new ServiceNotAvailable("Error while processing the ChatGPT payload string.");
    }

    HttpResponse response;
    try {
      response = restServiceHandler.execute(request);
      var statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != HttpStatus.SC_OK) {
        throw new ServiceNotAvailable("Get return " + statusCode + " from ChatGPT API.");
      }
    } catch (IOException e) {
      throw new ServiceNotAvailable("Error while connecting to CEDAR Terminology server.");
    }

    // Processing the response
    try {
      return restServiceHandler.processResponse(response);
    } catch (IOException e) {
      throw new ServiceNotAvailable("Error while processing the ChatGPT response string.");
    }
  }

  /*
  messages=[
          {"role": "system", "content": "You are a metadata spelling corrector. Given a metadata field name and list of permissible field values for HuBMAP, your task is to identify the best match for a specififed user input."},
          {"role": "user", "content": prompt},

      ]
   */
}
