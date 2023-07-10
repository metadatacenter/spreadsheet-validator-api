package org.metadatacenter.spreadsheetvalidator.thirdparty;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.metadatacenter.spreadsheetvalidator.exception.ValidatorServiceException;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
    return new StringBuilder()
        .append(cedarConfig.getRepoBaseUrl())
        .append("templates/")
        .append(id)
        .toString();
  }

  public ObjectNode getCedarTemplateFromIri(String iri) {
    // Prepare the request
    var url = generateTemplateUrlFromIri(iri);
    var request = restServiceHandler.createGetRequest(url, "apiKey " + cedarConfig.getApiKey());

    // Execute the request
    HttpResponse response = null;
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
    try {
      return new StringBuilder()
          .append(cedarConfig.getResourceBaseUrl())
          .append("templates/")
          .append(URLEncoder.encode(iri, Charsets.UTF_8.toString()))
          .toString();
    } catch (UnsupportedEncodingException e) {
      throw new ValidatorServiceException("Unable to construct the template IRI",
          e, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }
  }
}
