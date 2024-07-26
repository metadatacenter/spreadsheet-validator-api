package org.metadatacenter.spreadsheetvalidator;

import com.google.common.collect.ImmutableMap;
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
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.ContainerRequest;
import org.metadatacenter.spreadsheetvalidator.domain.ColumnDescription;
import org.metadatacenter.spreadsheetvalidator.domain.Spreadsheet;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetSchema;
import org.metadatacenter.spreadsheetvalidator.excel.ExcelBasedSchemaParser;
import org.metadatacenter.spreadsheetvalidator.excel.MetadataSpreadsheetBuilder;
import org.metadatacenter.spreadsheetvalidator.exception.ValidatorRuntimeException;
import org.metadatacenter.spreadsheetvalidator.exception.ValidatorServiceException;
import org.metadatacenter.spreadsheetvalidator.request.BadFileException;
import org.metadatacenter.spreadsheetvalidator.request.ValidateSpreadsheetRequest;
import org.metadatacenter.spreadsheetvalidator.request.ValidatorRequestBodyException;
import org.metadatacenter.spreadsheetvalidator.response.ErrorResponse;
import org.metadatacenter.spreadsheetvalidator.response.UrlCheckResponse;
import org.metadatacenter.spreadsheetvalidator.response.ValidateResponse;
import org.metadatacenter.spreadsheetvalidator.thirdparty.CedarService;
import org.metadatacenter.spreadsheetvalidator.thirdparty.RestServiceHandler;
import org.metadatacenter.spreadsheetvalidator.tsv.MissingMetadataSchemaIdException;

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
import java.util.Optional;
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

  private final MetadataSpreadsheetBuilder spreadsheetBuilder;

  private final SpreadsheetSchemaGenerator spreadsheetSchemaGenerator;

  private final ExcelBasedSchemaParser excelBasedSchemaParser;

  private final SpreadsheetValidator spreadsheetValidator;

  private final ValidationReportHandler validationReportHandler;

  private final ExcelConfig excelConfig;

  @Inject
  public ServiceResource(@Nonnull CedarService cedarService,
                         @Nonnull RestServiceHandler restServiceHandler,
                         @Nonnull MetadataSpreadsheetBuilder spreadsheetBuilder,
                         @Nonnull SpreadsheetSchemaGenerator spreadsheetSchemaGenerator,
                         @Nonnull ExcelBasedSchemaParser excelBasedSchemaParser,
                         @Nonnull SpreadsheetValidator spreadsheetValidator,
                         @Nonnull ValidationReportHandler validationReportHandler,
                         @Nonnull ExcelConfig excelConfig) {
    this.cedarService = checkNotNull(cedarService);
    this.restServiceHandler = checkNotNull(restServiceHandler);
    this.spreadsheetBuilder = checkNotNull(spreadsheetBuilder);
    this.spreadsheetSchemaGenerator = checkNotNull(spreadsheetSchemaGenerator);
    this.excelBasedSchemaParser = checkNotNull(excelBasedSchemaParser);
    this.spreadsheetValidator = checkNotNull(spreadsheetValidator);
    this.validationReportHandler = checkNotNull(validationReportHandler);
    this.excelConfig = checkNotNull(excelConfig);
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
      var spreadsheet = Spreadsheet.create(spreadsheetData);

      // Get the CEDAR template IRI
      var cedarTemplateIri = request.getCheckedCedarTemplateIri();
      var cedarTemplate = cedarService.getCedarTemplateFromIri(cedarTemplateIri);
      var spreadsheetSchema = spreadsheetSchemaGenerator.generateFrom(cedarTemplate);

      // Validate the spreadsheet based on its schema
      var validationReport = doValidation(spreadsheetSchema, spreadsheet);
      return getResponse(headers, spreadsheetSchema, spreadsheet, validationReport);
    } catch (ValidatorRequestBodyException e) {
      logError(headers, e.getResponse().getStatus(), e.getCause().getMessage());
      return responseErrorMessage(e);
    }
  }

  private void logUsage(HttpHeaders headers, String cedarTemplateIri, ValidationReport reporting) {
    var usageReport = UsageLog.create(
        ((ContainerRequest) headers).getAbsolutePath().toString(),
        Instant.now().toString(),
        Optional.ofNullable(headers.getRequestHeader("X-Forwarded-For"))
            .flatMap(values -> values.stream().findFirst())
            .orElse(""),
        Optional.ofNullable(headers.getRequestHeader("User-Agent"))
            .flatMap(values -> values.stream().findFirst())
            .orElse(""),
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
        Optional.ofNullable(headers.getRequestHeader("X-Forwarded-For"))
            .flatMap(values -> values.stream().findFirst())
            .orElse(""),
        Optional.ofNullable(headers.getRequestHeader("User-Agent"))
            .flatMap(values -> values.stream().findFirst())
            .orElse(""),
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
      var spreadsheet = Spreadsheet.create(spreadsheetData);

      // Get the CEDAR template ID
      var templateId = getMetadataSchemaId(spreadsheetData);
      var cedarTemplate = cedarService.getCedarTemplateFromId(templateId);
      var spreadsheetSchema = spreadsheetSchemaGenerator.generateFrom(cedarTemplate);

      // Validate the spreadsheet based on its schema
      var validationReport = doValidation(spreadsheetSchema, spreadsheet);
      return getResponse(headers, spreadsheetSchema, spreadsheet, validationReport);
    } catch (BadFileException e) {
      logError(headers, e.getResponse().getStatus(), e.getCause().getMessage());
      return responseErrorMessage(e);
    }
  }

  private ValidationReport doValidation(SpreadsheetSchema spreadsheetSchema, Spreadsheet spreadsheet) {
    return spreadsheetValidator
        .validate(spreadsheet, spreadsheetSchema)
        .collect(validationReportHandler);
  }

  private Response getResponse(HttpHeaders headers, SpreadsheetSchema schema, Spreadsheet spreadsheet, ValidationReport reporting) {
    try {
      var response = reporting.isEmpty()
          ? ValidateResponse.createSuccess(schema, spreadsheet, reporting)
          : ValidateResponse.createFailed(schema, spreadsheet, reporting);
      logUsage(headers, schema.getTemplateIri(), reporting);
      return Response.ok(response).build();
    } catch (ValidatorServiceException e) {
      logError(headers, schema.getTemplateIri(), e.getResponse().getStatus(), e.getCause().getMessage());
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
      throw new BadFileException("Bad TSV file.", new MissingMetadataSchemaIdException());
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
      var metadataSpreadsheet = spreadsheetBuilder.openSpreadsheetFromInputStream(inputStream).build();

      // Find the CEDAR template IRI
      var templateIri = metadataSpreadsheet.getProvenanceSheet().getSchemaIri();
      var cedarTemplate = cedarService.getCedarTemplateFromIri(templateIri);
      var spreadsheetSchema = spreadsheetSchemaGenerator.generateFrom(cedarTemplate);
      var columnMapping = getColumnMapping(spreadsheetSchema);

      // Get the data record table from the data sheet.
      var dataRecordTable = metadataSpreadsheet.getDataSheet().getDataRecordTable();
      var spreadsheetData = dataRecordTable.asMaps(columnMapping);
      var spreadsheet = Spreadsheet.create(spreadsheetData);

      // Validate the spreadsheet based on its schema
      var validationReport = doValidation(spreadsheetSchema, spreadsheet);
      return getResponse(headers, spreadsheetSchema, spreadsheet, validationReport);
    } catch (BadFileException e) {
      logError(headers, e.getResponse().getStatus(), e.getCause().getMessage());
      return responseErrorMessage(e);
    }
  }

  @POST
  @Operation(summary = "Validate a collection of metadata records in an Excel file according to the rules specified " +
      "in the column headers.")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/validate-structured-xlsx")
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
  public Response validateStructuredXlsx(
      @Context HttpHeaders headers,
      @Parameter(schema = @Schema(
          type = "string",
          format = "binary",
          description = "An Excel (.xlsx) file with metadata records and input rule headers",
          requiredMode = Schema.RequiredMode.REQUIRED)) @FormDataParam("input_file") InputStream inputStream,
      @Parameter(hidden = true) @FormDataParam("input_file") FormDataContentDisposition fileDetail) {
    try {
      // Retrieve the CEDAR template IRI about the spreadsheet data schema
      var templateIri = excelConfig.getMetaSchemaIri();
      var cedarTemplate = cedarService.getCedarTemplateFromIri(templateIri);
      var schemaTableSchema = spreadsheetSchemaGenerator.generateFrom(cedarTemplate);

      // Parse the input Excel file
      var metadataSpreadsheet = spreadsheetBuilder
          .openSpreadsheetFromInputStream(inputStream)
          .includeDataSchema()
          .build();

      // Get the data schema table from the data sheet.
      var dataSheet = metadataSpreadsheet.getDataSheet();
      var dataSchemaTable = dataSheet.getUncheckedSchemaTable();
      var schemaTableData = dataSchemaTable.asMaps();
      var schemaSpreadsheet = Spreadsheet.create(schemaTableData);

      // Validate the data schema table and check the response
      var schemaValidationReport = doValidation(schemaTableSchema, schemaSpreadsheet);
      if (!schemaValidationReport.isEmpty()) {
        return getResponse(headers, schemaTableSchema, schemaSpreadsheet, schemaValidationReport);
      }

      // Extract the data schema
      var dataSchema = excelBasedSchemaParser.extractTableSchemaFrom(dataSheet);

      // Get the data record table from the data sheet.
      var dataRecordTable = dataSheet.getDataRecordTable();
      var columnMapping = getColumnMapping(dataSchema);
      var recordTableData = dataRecordTable.asMaps(columnMapping);
      var recordSpreadsheet = Spreadsheet.create(recordTableData);

      var dataValidationReport = doValidation(dataSchema, recordSpreadsheet);
      return getResponse(headers, dataSchema, recordSpreadsheet, dataValidationReport);
    } catch (BadFileException e) {
      logError(headers, e.getResponse().getStatus(), e.getCause().getMessage());
      return responseErrorMessage(e);
    }
  }

  private Map<String, String> getColumnMapping(SpreadsheetSchema schema) {
    var columnDescriptionList = schema.getColumnDescription().values();
    return columnDescriptionList.stream()
        .collect(ImmutableMap.toImmutableMap(
            ColumnDescription::getColumnLabel,
            ColumnDescription::getColumnName
        ));
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
