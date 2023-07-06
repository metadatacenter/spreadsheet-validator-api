package org.metadatacenter.spreadsheetvalidator.thirdparty;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;

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
    try {
      var request = makeGetDescendantsFromBranchRequest(ontologyAcronym, branchUri);
      var response = restServiceHandler.execute(request);
      var responseObject = processResponse(response, branchUri);
      var collectionItems = responseObject.get("collection").spliterator();
      var collectionStream = StreamSupport.stream(collectionItems, false);
      return collectionStream.collect(Collectors.toMap(
          item -> item.get("prefLabel").asText(),
          item -> item.get("@id").asText()
      ));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Request makeGetDescendantsFromBranchRequest(@Nonnull String ontologyAcronym, @Nonnull URI branchUri) throws IOException {
    var baseUrl = bioPortalConfig.getBaseUrl();
    var url = new StringBuilder(baseUrl)
        .append("/ontologies/")
        .append(ontologyAcronym)
        .append("/classes/")
        .append(URLEncoder.encode(branchUri.toString(), StandardCharsets.UTF_8))
        .append("/descendants");
    var request = restServiceHandler.createGetRequest(
        url.toString(),
        "apiKey token=" + bioPortalConfig.getApiKey());
    return request;
  }

  private ObjectNode processResponse(HttpResponse response, URI branchUri) throws TemplateAccessException {
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
        throw new TemplateAccessException(branchUri.toString());
    }
  }
}
