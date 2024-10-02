package org.metadatacenter.spreadsheetvalidator.thirdparty;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;
import jakarta.ws.rs.core.Response;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
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

  public boolean checkCedarTemplateExists(String templateId) {
    var templateIri = generateTemplateIriFromId(templateId);
    var url = generateTemplateDetailsCall(templateIri);
    var request = createCedarRequest(url);

    var response = executeRequest(request);
    return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
  }

  public ObjectNode getCedarTemplateFromId(String id) {
    var iri = generateTemplateIriFromId(id);
    return getCedarTemplateFromIri(iri);
  }

  private String generateTemplateIriFromId(String id) {
    return cedarConfig.getRepoBaseUrl() + "templates/" + id;
  }

  public ObjectNode getCedarTemplateFromIri(String iri) {
    var url = generateGetTemplateCall(iri);
    var request = createCedarRequest(url);
    var response = executeRequest(request);

    validateResponse(response);
    return processCedarResponse(response);
  }

  private Request createCedarRequest(String url) {
    return restServiceHandler.createGetRequest(url, "apiKey " + cedarConfig.getApiKey());
  }

  private HttpResponse executeRequest(Request request) {
    try {
      return restServiceHandler.execute(request);
    } catch (IOException e) {
      throw new ValidatorServiceException("Error while connecting to CEDAR Resource server.",
        e, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }
  }

  private void validateResponse(HttpResponse response) {
    int statusCode = response.getStatusLine().getStatusCode();
    if (statusCode != HttpStatus.SC_OK) {
      String causeMessage = getResponseContent(response);
      throw new ValidatorServiceException(
        "Failed to retrieve the metadata specification used for the validation.",
        new IOException(causeMessage), statusCode);
    }
  }

  private String getResponseContent(HttpResponse response) {
    try {
      return new String(EntityUtils.toByteArray(response.getEntity()));
    } catch (IOException e) {
      throw new ValidatorServiceException("Error reading response content.",
        e, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }
  }

  private ObjectNode processCedarResponse(HttpResponse response) {
    try {
      return (ObjectNode) restServiceHandler.processResponse(response);
    } catch (IOException e) {
      throw new ValidatorServiceException("Failed to process response from CEDAR Resource server",
        e, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }
  }

  private String generateGetTemplateCall(String templateIri) {
    return cedarConfig.getResourceBaseUrl() +
      "templates/" +
      URLEncoder.encode(templateIri, Charsets.UTF_8);
  }

  private String generateTemplateDetailsCall(String templateIri) {
    return cedarConfig.getResourceBaseUrl() +
      "templates/" +
      URLEncoder.encode(templateIri, Charsets.UTF_8) +
      "/details/";
  }
}
