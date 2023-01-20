package org.metadatacenter.spreadsheetvalidator;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

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

  public ObjectNode getCedarTemplate(String iri) {
    HttpResponse response = null;
    // Execute the request
    try {
      var url = generateTemplateUrlFromIri(iri);
      var request = restServiceHandler.createGetRequest(
          url,
          "apiKey " + cedarConfig.getApiKey());
      response = restServiceHandler.execute(request);
    } catch (IOException e) {
      throw new BadRequestException(e);
    }
    // Process the response
    try {
      return processResponse(response);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private ObjectNode processResponse(HttpResponse response) throws IOException {
    var statusLine = response.getStatusLine();
    switch (statusLine.getStatusCode()) {
      case HttpStatus.SC_OK:
        var jsonString = new String(EntityUtils.toByteArray(response.getEntity()));
        return (ObjectNode) restServiceHandler.parseJsonString(jsonString);
      case HttpStatus.SC_NOT_FOUND:
        throw new FileNotFoundException(format(
            "Couldn't find CEDAR template. Cause: %s", statusLine));
      default:
        throw new BadRequestException(format(
            "Error retrieving template. Cause: %s", statusLine));
    }
  }

  private String generateTemplateUrlFromIri(String iri) {
    try {
      return new StringBuilder()
          .append(cedarConfig.getBaseUrl())
          .append("templates/")
          .append(URLEncoder.encode(iri, Charsets.UTF_8.toString()))
          .toString();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
