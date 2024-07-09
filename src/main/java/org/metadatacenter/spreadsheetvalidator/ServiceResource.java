package org.metadatacenter.spreadsheetvalidator;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.ContainerRequest;
import org.metadatacenter.spreadsheetvalidator.domain.Spreadsheet;
import org.metadatacenter.spreadsheetvalidator.exception.ValidatorRuntimeException;
import org.metadatacenter.spreadsheetvalidator.exception.ValidatorServiceException;
import org.metadatacenter.spreadsheetvalidator.request.BadFileException;
import org.metadatacenter.spreadsheetvalidator.request.MissingDerivedFromColumnException;
import org.metadatacenter.spreadsheetvalidator.request.MissingMetadataSchemaIdColumnException;
import org.metadatacenter.spreadsheetvalidator.request.MissingMetadataSheetException;
import org.metadatacenter.spreadsheetvalidator.request.ValidateSpreadsheetRequest;
import org.metadatacenter.spreadsheetvalidator.request.ValidatorRequestBodyException;
import org.metadatacenter.spreadsheetvalidator.response.ErrorResponse;
import org.metadatacenter.spreadsheetvalidator.response.UrlCheckResponse;
import org.metadatacenter.spreadsheetvalidator.response.ValidateResponse;
import org.metadatacenter.spreadsheetvalidator.thirdparty.CedarService;
import org.metadatacenter.spreadsheetvalidator.thirdparty.RestServiceHandler;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Path("/service")
@Produces(MediaType.APPLICATION_JSON)
public class ServiceResource {

  private final CedarService cedarService;

  private final RestServiceHandler restServiceHandler;

  private final ExcelFileHandler excelFileHandler;

  private final SpreadsheetSchemaGenerator spreadsheetSchemaGenerator;

  private final SpreadsheetValidator spreadsheetValidator;

  private final ResultCollector resultCollector;

  @Inject
  public ServiceResource(@Nonnull CedarService cedarService,
                         @Nonnull RestServiceHandler restServiceHandler,
                         @Nonnull ExcelFileHandler excelFileHandler,
                         @Nonnull SpreadsheetSchemaGenerator spreadsheetSchemaGenerator,
                         @Nonnull SpreadsheetValidator spreadsheetValidator,
                         @Nonnull ResultCollector resultCollector) {
    this.cedarService = checkNotNull(cedarService);
    this.restServiceHandler = checkNotNull(restServiceHandler);
    this.excelFileHandler = checkNotNull(excelFileHandler);
    this.spreadsheetSchemaGenerator = checkNotNull(spreadsheetSchemaGenerator);
    this.spreadsheetValidator = checkNotNull(spreadsheetValidator);
    this.resultCollector = checkNotNull(resultCollector);
  }

  @POST
  @Operation(summary = "Validate a set of metadata objects according to a metadata specification.")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/validate")
  @Tag(name = "Validation")
  @RequestBody(
      description = "A JSON object containing the spreadsheet data and CEDAR template ID string. The spreadsheet " +
          "data is a JSON representation of the input table data it forms as an array of objects where each " +
          "object is a row with the column name as the field name and the cell as the value.",
      required = true,
      content = @Content(
          schema = @Schema(implementation = ValidateSpreadsheetRequest.class),
          mediaType = MediaType.APPLICATION_JSON
      ))
  @ApiResponse(
      responseCode = "200",
      description = "A JSON object showing the validation report and other properties such as the" +
          "extracted schema and the original spreadsheet data.",
      content = @Content(
          schema = @Schema(implementation = ValidateResponse.class),
          mediaType = MediaType.APPLICATION_JSON
      ))
  @ApiResponse(
      responseCode = "400",
      description = "The request could not be understood by the server due to " +
          "malformed syntax in the request body.")
  @ApiResponse(
      responseCode = "500",
      description = "The server encountered an unexpected condition that prevented " +
          "it from fulfilling the request.")
  public Response validate(@Context HttpHeaders headers,
                           @Nonnull ValidateSpreadsheetRequest request) {
    try {
      // Get the spreadsheet data
      var spreadsheetData = request.getCheckedSpreadsheetData();

      // Get the CEDAR template IRI
      var cedarTemplateIri = request.getCheckedCedarTemplateIri();
      var cedarTemplate = cedarService.getCedarTemplateFromIri(cedarTemplateIri);

      // Validate the spreadsheet based on its schema
      return getResponse(headers, cedarTemplate, spreadsheetData);
    } catch (ValidatorRequestBodyException e) {
      logError(headers, e.getResponse().getStatus(), e.getCause().getMessage());
      return responseErrorMessage(e);
    }
  }

  private void logUsage(HttpHeaders headers, String cedarTemplateIri, ValidationReport reporting) {
    var usageReport = UsageLog.create(
        ((ContainerRequest) headers).getAbsolutePath().toString(),
        Instant.now().toString(),
        headers.getRequestHeader("X-Forwarded-For").stream().findFirst().orElse(""),
        headers.getRequestHeader("User-Agent").stream().findFirst().orElse(""),
        200,
        "Success",
        cedarTemplateIri,
        reporting
    );
    if (reporting.isEmpty()) {
      writeToFile(usageReport, "passed");
    } else {
      writeToFile(usageReport, "failed");
    }
  }

  private void logError(HttpHeaders headers, String cedarTemplateIri, int statusCode, String errorMessage) {
    var usageReport = UsageLog.create(
        ((ContainerRequest) headers).getAbsolutePath().toString(),
        Instant.now().toString(),
        headers.getRequestHeader("X-Forwarded-For").stream().findFirst().orElse(""),
        headers.getRequestHeader("User-Agent").stream().findFirst().orElse(""),
        statusCode,
        errorMessage,
        cedarTemplateIri,
        null
    );
    writeToFile(usageReport, "error");
  }

  private void logError(HttpHeaders headers, int statusCode, String errorMessage) {
    logError(headers, null, statusCode, errorMessage);
  }

  @POST
  @Operation(summary = "Validate a metadata TSV file according to a metadata specification.")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/validate-tsv")
  @Tag(name = "Validation")
  @ApiResponse(
      responseCode = "200",
      description = "A JSON object showing the validation report and other properties such as the" +
          "extracted schema and the original spreadsheet data.",
      content = @Content(
          schema = @Schema(implementation = ValidateResponse.class),
          mediaType = MediaType.APPLICATION_JSON
      ))
  @ApiResponse(
      responseCode = "400",
      description = "The request could not be understood by the server due to " +
          "malformed syntax in the request body.")
  @ApiResponse(
      responseCode = "500",
      description = "The server encountered an unexpected condition that prevented " +
          "it from fulfilling the request.")
  public Response validateTsv(
      @Context HttpHeaders headers,
      @Parameter(schema = @Schema(
          type = "string",
          format = "binary",
          description = "A TSV file with a mandatory column `metadata_schema_id` that contains the CEDAR template ID.",
          requiredMode = Schema.RequiredMode.REQUIRED)) @FormDataParam("input_file") InputStream inputStream,
      @Parameter(hidden = true) @FormDataParam("input_file") FormDataContentDisposition fileDetail) {
    try {
      // Parse the input TSV file
      var tsvString = getTsvString(inputStream);
      var spreadsheetData = parseTsvString(tsvString);

      // Get the CEDAR template ID
      var templateId = getMetadataSchemaId(spreadsheetData);
      var cedarTemplate = cedarService.getCedarTemplateFromId(templateId);

      // Validate the spreadsheet based on its schema
      return getResponse(headers, cedarTemplate, spreadsheetData);
    } catch (BadFileException e) {
      logError(headers, e.getResponse().getStatus(), e.getCause().getMessage());
      return responseErrorMessage(e);
    }
  }

  private Response getResponse(HttpHeaders headers, ObjectNode cedarTemplate, List<Map<String, Object>> spreadsheetData) {
    var spreadsheetSchema = spreadsheetSchemaGenerator.generateFrom(cedarTemplate);
    var spreadsheet = Spreadsheet.create(spreadsheetData);
    try {
      var reporting = spreadsheetValidator
          .additionalColumnsNotAllowed(spreadsheet, spreadsheetSchema)
          .validate(spreadsheet, spreadsheetSchema)
          .collect(resultCollector);
      var response = ValidateResponse.create(spreadsheetSchema, spreadsheet, reporting);
      logUsage(headers, spreadsheetSchema.getTemplateIri(), reporting);
      return Response.ok(response).build();
    } catch (ValidatorServiceException e) {
      logError(headers, spreadsheetSchema.getTemplateIri(), e.getResponse().getStatus(), e.getCause().getMessage());
      return responseErrorMessage(e);
    }
  }

  private String getTsvString(InputStream inputStream) {
    try {
      return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new BadFileException("Bad TSV file.", e);
    }
  }

  private List<Map<String, Object>> parseTsvString(String tsvString) {
    try {
      return restServiceHandler.parseTsvString(tsvString);
    } catch (IOException e) {
      throw new BadFileException("Bad TSV file.", e);
    }
  }

  private String getMetadataSchemaId(List<Map<String, Object>> spreadsheetData) {
    var metadataRow = spreadsheetData.stream()
        .findAny()
        .orElseThrow(() -> new BadFileException("Bad TSV file.", new IOException("The file is empty")));
    var metadataSchemaId = metadataRow.get("metadata_schema_id").toString();
    if (metadataSchemaId.trim().isEmpty()) {
      throw new BadFileException("Bad TSV file.", new MissingMetadataSchemaIdColumnException());
    }
    return metadataSchemaId;
  }

  @POST
  @Operation(summary = "Validate a metadata Excel file according to a metadata specification.")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/validate-xlsx")
  @Tag(name = "Validation")
  @ApiResponse(
      responseCode = "200",
      description = "A JSON object showing the validation report and other properties such as the" +
          "extracted schema and the original spreadsheet data.",
      content = @Content(
          schema = @Schema(implementation = ValidateResponse.class),
          mediaType = MediaType.APPLICATION_JSON
      ))
  @ApiResponse(
      responseCode = "400",
      description = "The request could not be understood by the server due to " +
          "malformed syntax in the request body.")
  @ApiResponse(
      responseCode = "500",
      description = "The server encountered an unexpected condition that prevented " +
          "it from fulfilling the request.")
  public Response validateXlsx(
      @Context HttpHeaders headers,
      @Parameter(schema = @Schema(
          type = "string",
          format = "binary",
          description = "An Excel (.xlsx) file with a mandatory `.metadata` sheet that contains the CEDAR template ID.",
          requiredMode = Schema.RequiredMode.REQUIRED)) @FormDataParam("input_file") InputStream inputStream,
      @Parameter(hidden = true) @FormDataParam("input_file") FormDataContentDisposition fileDetail) {
    try {
      // Parse the input Excel file
      var workbook = getWorkbook(inputStream);
      var mainSheet = getMainSheet(workbook);
      var spreadsheetData = excelFileHandler.getTableData(mainSheet);

      // Find the CEDAR template IRI
      var metadataSheet = getMetadataSheet(workbook);
      var templateIri = getTemplateIri(metadataSheet);
      var cedarTemplate = cedarService.getCedarTemplateFromIri(templateIri);

      // Validate the spreadsheet based on its schema
      return getResponse(headers, cedarTemplate, spreadsheetData);
    } catch (BadFileException e) {
      logError(headers, e.getResponse().getStatus(), e.getCause().getMessage());
      return responseErrorMessage(e);
    }
  }

  private XSSFWorkbook getWorkbook(InputStream is) {
    try {
      return excelFileHandler.getWorkbookFromInputStream(is);
    } catch (IOException e) {
      throw new BadFileException("Bad Excel file.", e);
    }
  }

  private XSSFSheet getMainSheet(XSSFWorkbook workbook) {
    var sheet = excelFileHandler.getSheet(workbook, "MAIN");
    if (sheet == null) {
      sheet = excelFileHandler.getSheet(workbook, 0);
    }
    return sheet;
  }

  private XSSFSheet getMetadataSheet(XSSFWorkbook workbook) {
    var sheet = excelFileHandler.getSheet(workbook, ".metadata");
    if (sheet == null) {
      throw new BadFileException("Bad Excel file.", new MissingMetadataSheetException());
    }
    return sheet;
  }

  private String getTemplateIri(XSSFSheet metadataSheet) {
    var columnIndex = getDerivedFromColumnLocation(metadataSheet);
    var templateIri = excelFileHandler.getStringCellValue(metadataSheet, 1, columnIndex);
    if (templateIri.trim().isEmpty()) {
      throw new BadFileException("Bad Excel file.", new MissingDerivedFromColumnException());
    }
    return templateIri;
  }

  private int getDerivedFromColumnLocation(XSSFSheet metadataSheet) {
    var derivedFromColumnLocation = excelFileHandler.findColumnLocation(metadataSheet, "pav:derivedFrom");
    if (!derivedFromColumnLocation.isPresent()) {
      throw new BadFileException("Bad Excel file.", new MissingDerivedFromColumnException());
    }
    return derivedFromColumnLocation.get();
  }

  private Response responseErrorMessage(ValidatorRuntimeException e) {
    var statusInfo = e.getResponse().getStatusInfo();
    var statusCode = statusInfo.getStatusCode();
    var response = ErrorResponse.create(e.getMessage(),
        e.getCause().getMessage(),
        statusCode + " " + statusInfo.getReasonPhrase(),
        e.getFixSuggestion().orElse(null));
    return Response.status(statusCode)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(response)
        .build();
  }

  private void writeToFile(UsageLog usageLog, String filePrefix) {
    try {
      var signature = UUID.randomUUID();
      var logFile = new File("usage-logs/" + filePrefix + "-" + signature + ".json");
      restServiceHandler.writeObjectToFile(usageLog, logFile);
    } catch (IOException e) {
      System.err.println("Failed to write the log. Cause: " + e.getMessage());
    }
  }

  @POST
  @Operation(summary = "Check if a URL exists or not.")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/url-checker")
  @Tag(name = "Utility")
  @ApiResponse(
      responseCode = "200",
      description = "A JSON object showing the answer.",
      content = @Content(
          schema = @Schema(implementation = UrlCheckResponse.class),
          mediaType = MediaType.APPLICATION_JSON
      ))
  @ApiResponse(
      responseCode = "400",
      description = "The request could not be understood by the server due to " +
          "malformed syntax in the request body.")
  @ApiResponse(
      responseCode = "500",
      description = "The server encountered an unexpected condition that prevented " +
          "it from fulfilling the request.")
  public Response urlChecker(@Nonnull String urlString) {
    try {
      var url = new URL(urlString);
      var conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      int responseCode = conn.getResponseCode();
      var answer = UrlCheckResponse.create(urlString, responseCode == HttpURLConnection.HTTP_OK);
      return Response.ok(answer).build();
    } catch (IOException e) {
      var answer = UrlCheckResponse.create(urlString, false);
      return Response.ok(answer).build();
    }
  }
}
