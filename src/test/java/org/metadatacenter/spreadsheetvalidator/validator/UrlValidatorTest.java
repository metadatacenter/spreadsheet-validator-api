package org.metadatacenter.spreadsheetvalidator.validator;

import com.google.common.collect.ImmutableList;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metadatacenter.spreadsheetvalidator.GeneralConfig;
import org.metadatacenter.spreadsheetvalidator.RepairClosures;
import org.metadatacenter.spreadsheetvalidator.ValidationResult;
import org.metadatacenter.spreadsheetvalidator.ValidationSettings;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.domain.ColumnDescription;
import org.metadatacenter.spreadsheetvalidator.domain.ValueType;
import java.io.IOException;
import java.net.InetSocketAddress;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

class UrlValidatorTest {

  private static HttpServer server;
  private static int port;

  private UrlValidator urlValidator;
  private ValidationResult validationResult;
  private ValidatorContext validatorContext;
  private ValueContext valueContext;

  @BeforeAll
  static void startServer() throws IOException {
    server = HttpServer.create(new InetSocketAddress(0), 0);
    port = server.getAddress().getPort();

    // Returns 200 OK
    server.createContext("/ok", exchange -> {
      exchange.sendResponseHeaders(200, -1);
      exchange.close();
    });

    // Returns 404 Not Found
    server.createContext("/not-found", exchange -> {
      exchange.sendResponseHeaders(404, -1);
      exchange.close();
    });

    // Redirects (301) to /ok
    server.createContext("/redirect-301", exchange -> {
      exchange.getResponseHeaders().set("Location", "http://localhost:" + port + "/ok");
      exchange.sendResponseHeaders(301, -1);
      exchange.close();
    });

    // Redirects (302) to /ok
    server.createContext("/redirect-302", exchange -> {
      exchange.getResponseHeaders().set("Location", "http://localhost:" + port + "/ok");
      exchange.sendResponseHeaders(302, -1);
      exchange.close();
    });

    // Redirects (307) to /ok
    server.createContext("/redirect-307", exchange -> {
      exchange.getResponseHeaders().set("Location", "http://localhost:" + port + "/ok");
      exchange.sendResponseHeaders(307, -1);
      exchange.close();
    });

    // Redirects (308) to /ok
    server.createContext("/redirect-308", exchange -> {
      exchange.getResponseHeaders().set("Location", "http://localhost:" + port + "/ok");
      exchange.sendResponseHeaders(308, -1);
      exchange.close();
    });

    // Redirects to a 404 page
    server.createContext("/redirect-to-404", exchange -> {
      exchange.getResponseHeaders().set("Location", "http://localhost:" + port + "/not-found");
      exchange.sendResponseHeaders(301, -1);
      exchange.close();
    });

    // Returns 404 for HEAD but 200 for GET (simulates servers that don't support HEAD)
    server.createContext("/head-unsupported", exchange -> {
      if ("HEAD".equalsIgnoreCase(exchange.getRequestMethod())) {
        exchange.sendResponseHeaders(404, -1);
      } else {
        exchange.sendResponseHeaders(200, -1);
      }
      exchange.close();
    });

    // Chain redirect: /redirect-chain -> /redirect-301 -> /ok
    server.createContext("/redirect-chain", exchange -> {
      exchange.getResponseHeaders().set("Location", "http://localhost:" + port + "/redirect-301");
      exchange.sendResponseHeaders(302, -1);
      exchange.close();
    });

    server.start();
  }

  @AfterAll
  static void stopServer() {
    server.stop(0);
  }

  @BeforeEach
  void setUp() {
    urlValidator = new UrlValidator();
    validationResult = new ValidationResult();
    var generalConfig = GeneralConfig.create("ASCII", "metadata_schema_id");
    validatorContext = new ValidatorContext(
        new RepairClosures(), validationResult, new ValidationSettings(generalConfig));
    var columnDescription = ColumnDescription.create(
        "url_column", "URL Column", ValueType.URL, null,
        null, null, false, "A URL field",
        null, null, ImmutableList.of());
    valueContext = ValueContext.create("url_column", 1, columnDescription);
  }

  @Test
  void shouldPassForDirectUrl() {
    urlValidator.validateInputValue(
        "http://localhost:" + port + "/ok", valueContext, validatorContext);
    assertThat(validationResult.getErrorList(), is(empty()));
  }

  @Test
  void shouldFailForNotFoundUrl() {
    urlValidator.validateInputValue(
        "http://localhost:" + port + "/not-found", valueContext, validatorContext);
    assertThat(validationResult.getErrorList(), hasSize(1));
  }

  @Test
  void shouldPassForRedirect301() {
    urlValidator.validateInputValue(
        "http://localhost:" + port + "/redirect-301", valueContext, validatorContext);
    assertThat(validationResult.getErrorList(), is(empty()));
  }

  @Test
  void shouldPassForRedirect302() {
    urlValidator.validateInputValue(
        "http://localhost:" + port + "/redirect-302", valueContext, validatorContext);
    assertThat(validationResult.getErrorList(), is(empty()));
  }

  @Test
  void shouldPassForRedirect307() {
    urlValidator.validateInputValue(
        "http://localhost:" + port + "/redirect-307", valueContext, validatorContext);
    assertThat(validationResult.getErrorList(), is(empty()));
  }

  @Test
  void shouldPassForRedirect308() {
    urlValidator.validateInputValue(
        "http://localhost:" + port + "/redirect-308", valueContext, validatorContext);
    assertThat(validationResult.getErrorList(), is(empty()));
  }

  @Test
  void shouldFailForRedirectToNotFound() {
    urlValidator.validateInputValue(
        "http://localhost:" + port + "/redirect-to-404", valueContext, validatorContext);
    assertThat(validationResult.getErrorList(), hasSize(1));
  }

  @Test
  void shouldPassWhenHeadReturns404ButGetReturns200() {
    urlValidator.validateInputValue(
        "http://localhost:" + port + "/head-unsupported", valueContext, validatorContext);
    assertThat(validationResult.getErrorList(), is(empty()));
  }

  @Test
  void shouldPassForChainedRedirects() {
    urlValidator.validateInputValue(
        "http://localhost:" + port + "/redirect-chain", valueContext, validatorContext);
    assertThat(validationResult.getErrorList(), is(empty()));
  }

  @Test
  void shouldFailForMalformedUrl() {
    urlValidator.validateInputValue(
        "not-a-url", valueContext, validatorContext);
    assertThat(validationResult.getErrorList(), hasSize(1));
  }
}
