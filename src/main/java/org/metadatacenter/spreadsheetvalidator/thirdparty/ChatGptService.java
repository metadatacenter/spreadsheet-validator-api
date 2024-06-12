package org.metadatacenter.spreadsheetvalidator.thirdparty;

import autovalue.shaded.com.google.common.collect.ImmutableMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.collect.ImmutableList;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.jetbrains.annotations.NotNull;

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

  private final Cache<String, String> serviceResultCache;

  @Inject
  public ChatGptService(@Nonnull ChatGptConfig chatGptConfig,
                        @Nonnull RestServiceHandler restServiceHandler,
                        @Nonnull Cache<String, String> serviceResultCache) {
    this.chatGptConfig = checkNotNull(chatGptConfig);
    this.restServiceHandler = checkNotNull(restServiceHandler);
    this.serviceResultCache = checkNotNull(serviceResultCache);
  }

  public String getResponse(@Nonnull String prompt) throws ServiceNotAvailable {
    try {
      return serviceResultCache.get(prompt, k -> makeRemoteCall(prompt));
    } catch (RuntimeException e) {
      throw new ServiceNotAvailable(e.getMessage());
    }
  }

  private String makeRemoteCall(@NotNull String prompt) {
    Request request;
    String payloadString = "";
    try {
      var payload = ImmutableMap.of(
          "model", chatGptConfig.getModel(),
          "temperature", 0.35,
          "messages", ImmutableList.of(
              ImmutableMap.of(
                  "role", "system",
                  "content", "You are a spelling checker. Your task is to identify the closest match " +
                      "from a list of permitted values for a given field based on a specified user input. " +
                      "Return the best matching value directly from your output."),
              ImmutableMap.of(
                  "role", "user",
                  "content", prompt))
      );
      payloadString = restServiceHandler.writeJsonString(payload);
      request = restServiceHandler.createPostRequest(chatGptConfig.getBaseUrl(),
          "Bearer " + chatGptConfig.getApiKey(),
          payloadString);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error while processing the ChatGPT payload string.");
    }

    HttpResponse response;
    try {
      response = restServiceHandler.execute(request);
      var statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != HttpStatus.SC_OK) {
        throw new RuntimeException("Get return " + statusCode + " from ChatGPT API.");
      }
    } catch (IOException e) {
      throw new RuntimeException("Error while connecting to CEDAR Terminology server.");
    }

    // Processing the response
    try {
      var responseObject = restServiceHandler.processResponse(response);
      return responseObject.get("choices").get(0).get("message").get("content").asText();
    } catch (IOException e) {
      throw new RuntimeException("Error while processing the ChatGPT response string.");
    }
  }
}
