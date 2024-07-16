package org.metadatacenter.spreadsheetvalidator.thirdparty;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;
import jakarta.ws.rs.core.Response;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.metadatacenter.spreadsheetvalidator.exception.ValidatorServiceException;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URLEncoder;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CedarService {

  private final CedarConfig cedarConfig;

  private final RestServiceHandler restServiceHandler;

  @Inject
  public CedarService(CedarConfig cedarConfig,
                      RestServiceHandler restServiceHandler) {
    this.cedarConfig = checkNotNull(cedarConfig);
    this.restServiceHandler = checkNotNull(restServiceHandler);
  }

  public ObjectNode getCedarTemplateFromId(String id) {
    var iri = generateTemplateIriFromId(id);
    return getCedarTemplateFromIri(iri);
  }

  private String generateTemplateIriFromId(String id) {
    return cedarConfig.getRepoBaseUrl() + "templates/" + id;
  }

  public ObjectNode getCedarTemplateFromIri(String iri) {
    // Prepare the request
    var url = generateTemplateUrlFromIri(iri);
    var request = restServiceHandler.createGetRequest(url, "apiKey " + cedarConfig.getApiKey());

    // Execute the request
    HttpResponse response;
    try {
      response = restServiceHandler.execute(request);
      var statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != HttpStatus.SC_OK) {
        var causeMessage = new String(EntityUtils.toByteArray(response.getEntity()));
        var cause = new IOException(causeMessage);
        throw new ValidatorServiceException("Failed to retrieve the metadata specification used for the validation.", cause, statusCode);
      }
    } catch (IOException e) {
      throw new ValidatorServiceException("Error while connecting to CEDAR Resource server.",
          e, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }
    // Process the response
    try {
      return (ObjectNode) restServiceHandler.processResponse(response);
    } catch (IOException e) {
      throw new ValidatorServiceException("Failed to process response from CEDAR Resource server",
          e, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }
  }

  private String generateTemplateUrlFromIri(String iri) {
    return cedarConfig.getResourceBaseUrl() + "templates/" + URLEncoder.encode(iri, Charsets.UTF_8);
  }
}
