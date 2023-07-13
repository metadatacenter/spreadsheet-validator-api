package org.metadatacenter.spreadsheetvalidator.thirdparty;

import jakarta.ws.rs.core.Response;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.metadatacenter.spreadsheetvalidator.exception.ValidatorServiceException;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class BioPortalService {

  private final BioPortalConfig bioPortalConfig;

  private final RestServiceHandler restServiceHandler;

  @Inject
  public BioPortalService(@Nonnull BioPortalConfig bioPortalConfig,
                          @Nonnull RestServiceHandler restServiceHandler) {
    this.bioPortalConfig = checkNotNull(bioPortalConfig);
    this.restServiceHandler = checkNotNull(restServiceHandler);
  }

  public Map<String, String> getDescendantsFromBranch(@Nonnull String ontologyAcronym, @Nonnull URI branchUri) {
    // Prepare the request
    var url = generateRequestUrl(ontologyAcronym, branchUri);
    var request = restServiceHandler.createGetRequest(url, "apiKey token=" + bioPortalConfig.getApiKey());

    // Execute the request
    HttpResponse response = null;
    try {
      response = restServiceHandler.execute(request);
      var statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != HttpStatus.SC_OK) {
        var causeMessage = new String(EntityUtils.toByteArray(response.getEntity()));
        var cause = new IOException(causeMessage);
        throw new ValidatorServiceException(
            "Failed to retrieve categorical values from ontology branch: " + branchUri + " (" + ontologyAcronym + ").",
            cause, statusCode);
      }
    } catch (IOException e) {
      throw new ValidatorServiceException("Error while connecting to BioPortal.",
          e, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    // Process the response
    try {
      var responseObject = restServiceHandler.processResponse(response);
      var collectionItems = responseObject.get("collection").spliterator();
      var collectionStream = StreamSupport.stream(collectionItems, false);
      return collectionStream.collect(Collectors.toMap(
          item -> item.get("prefLabel").asText(),
          item -> item.get("@id").asText()
      ));
    } catch (IOException e) {
      throw new ValidatorServiceException("Failed to process response from BioPortal.",
          e, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }
  }

  private String generateRequestUrl(@Nonnull String ontologyAcronym, @Nonnull URI branchUri) {
    var baseUrl = bioPortalConfig.getBaseUrl();
    return new StringBuilder(baseUrl)
        .append("/ontologies/")
        .append(ontologyAcronym)
        .append("/classes/")
        .append(URLEncoder.encode(branchUri.toString(), StandardCharsets.UTF_8))
        .append("/descendants")
        .toString();
  }
}
