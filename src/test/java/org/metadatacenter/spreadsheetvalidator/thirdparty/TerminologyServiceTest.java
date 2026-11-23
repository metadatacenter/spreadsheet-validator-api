package org.metadatacenter.spreadsheetvalidator.thirdparty;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicHttpResponse;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasKey;
import static org.mockito.Mockito.mock;

class TerminologyServiceTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final ValueConstraints CONSTRAINTS = ControlledTermValueConstraints.builder().build();

  /** Answers each request with the next queued page and keeps the payload it was sent. */
  private static class PagedHandler extends RestServiceHandler {

    private final Deque<JsonNode> pages = new ArrayDeque<>();
    private final List<Map<?, ?>> payloads = new ArrayList<>();

    PagedHandler() {
      super(MAPPER, MAPPER.reader());
    }

    @Override
    public String writeJsonString(Object o) {
      payloads.add((Map<?, ?>) o);
      return "{}";
    }

    @Override
    public Request createPostRequest(String uri, String apiKey, String jsonPayload) {
      return Request.Post(uri);
    }

    @Override
    public HttpResponse execute(Request request) {
      return new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
    }

    @Override
    public JsonNode processResponse(HttpResponse response) {
      return pages.removeFirst();
    }

    @Override
    public <T> T writeObject(JsonNode jsonNode, Class<T> objectType) {
      return mock(objectType);
    }
  }

  private static JsonNode page(int count, Integer totalCount) {
    ObjectNode page = MAPPER.createObjectNode();
    ArrayNode collection = page.putArray("collection");
    for (int i = 0; i < count; i++) {
      collection.addObject().put("@id", "https://example.org/v" + i);
    }
    if (totalCount != null) {
      page.put("totalCount", totalCount);
    }
    return page;
  }

  private static TerminologyService service(PagedHandler handler, int pageSize) {
    var config = CedarConfig.create("key", "https://repo.example/", "https://resource.example/",
        "https://terminology.example/bioportal/integrated-search", pageSize, 1, 1);
    return new TerminologyService(config, handler);
  }

  @Test
  void aValueSetIsReadByLimitAndOffsetUntilAPageComesBackShort() {
    var handler = new PagedHandler();
    handler.pages.add(page(500, null));
    handler.pages.add(page(500, null));
    handler.pages.add(page(20, null));

    var values = service(handler, 500).getOntologyValues("field", CONSTRAINTS);

    assertThat(values.size(), is(1020));
    assertThat(handler.payloads.stream().map(p -> p.get("offset")).toList(), contains(0, 500, 1000));
    for (Map<?, ?> payload : handler.payloads) {
      assertThat(payload.get("limit"), is(500));
      assertThat(payload, not(hasKey("page")));
      assertThat(payload, not(hasKey("pageSize")));
    }
  }

  @Test
  void aFullPageThatReachesTheTotalIsTheLast() {
    var handler = new PagedHandler();
    handler.pages.add(page(500, 500));

    assertThat(service(handler, 500).getOntologyValues("field", CONSTRAINTS).size(), is(500));
    assertThat(handler.payloads.size(), is(1));
  }

  @Test
  void aConfiguredPageSizeAboveTheServersMaximumIsReducedToIt() {
    var handler = new PagedHandler();
    handler.pages.add(page(3, 3));

    service(handler, 1000).getOntologyValues("field", CONSTRAINTS);

    assertThat(handler.payloads.get(0).get("limit"), is(TerminologyService.MAX_PAGE_SIZE));
  }
}
