package org.metadatacenter.spreadsheetvalidator;

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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.metadatacenter.spreadsheetvalidator.domain.Spreadsheet;
import org.metadatacenter.spreadsheetvalidator.exception.ValidatorRuntimeException;
import org.metadatacenter.spreadsheetvalidator.request.ValidateSpreadsheetRequest;
import org.metadatacenter.spreadsheetvalidator.response.ErrorResponse;
import org.metadatacenter.spreadsheetvalidator.response.ValidateResponse;
import org.metadatacenter.spreadsheetvalidator.thirdparty.CedarService;
import org.metadatacenter.spreadsheetvalidator.thirdparty.RestServiceHandler;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

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
  public Response validate(@Nonnull ValidateSpreadsheetRequest request) {
    try {
      var cedarTemplateIri = request.getCedarTemplateIri();
      var cedarTemplate = cedarService.getCedarTemplateFromIri(cedarTemplateIri);
      var spreadsheetSchema = spreadsheetSchemaGenerator.generateFrom(cedarTemplate);
      var spreadsheetData = request.getSpreadsheetData();
      var spreadsheet = Spreadsheet.create(spreadsheetData);
      var reporting = spreadsheetValidator
          .validate(spreadsheet, spreadsheetSchema)
          .collect(resultCollector);
      var response = ValidateResponse.create(spreadsheetSchema, spreadsheet, reporting);
      return Response.ok(response).build();
    } catch (ValidatorRuntimeException e) {
      return responseErrorMessage(e);
    }
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
      @Parameter(schema = @Schema(
          type = "string",
          format = "binary",
          description = "A TSV file with a mandatory column `metadata_schema_id` that contains the CEDAR template ID.",
          requiredMode = Schema.RequiredMode.REQUIRED)) @FormDataParam("input_file") InputStream inputStream,
      @Parameter(hidden = true) @FormDataParam("input_file") FormDataContentDisposition fileDetail) {
    try {
      var tsvString = getTsvString(inputStream);
      var spreadsheetData = parseTsvString(tsvString);
      var templateId = getTemplateId(spreadsheetData);
      var cedarTemplate = cedarService.getCedarTemplateFromId(templateId);
      var spreadsheetSchema = spreadsheetSchemaGenerator.generateFrom(cedarTemplate);
      var spreadsheet = Spreadsheet.create(spreadsheetData);
      var reporting = spreadsheetValidator
          .validate(spreadsheet, spreadsheetSchema)
          .collect(resultCollector);
      var response = ValidateResponse.create(spreadsheetSchema, spreadsheet, reporting);
      return Response.ok(response).build();
    } catch (ValidatorRuntimeException e) {
      return responseErrorMessage(e);
    }
  }

  private String getTsvString(InputStream inputStream) {
    try {
      return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new InvalidInputFileException("Invalid metadata TSV file.", e);
    }
  }

  private List<Map<String, Object>> parseTsvString(String tsvString) {
    try {
      return restServiceHandler.parseTsvString(tsvString);
    } catch (IOException e) {
      throw new InvalidInputFileException("Invalid metadata TSV file.", e);
    }
  }

  private String getTemplateId(List<Map<String, Object>> spreadsheetData) {
    var metadataRow = spreadsheetData.stream()
        .findAny()
        .orElseThrow(() -> new InvalidInputFileException(
            "Invalid metadata TSV file.",
            new IllegalArgumentException("The file is empty")));
    var templateId = metadataRow.get("metadata_schema_id").toString();
    if (templateId.trim().isEmpty()) {
      throw new InvalidInputFileException(
          "Invalid metadata TSV file.",
          new IllegalArgumentException("The metadata_schema_id is missing in the file."));
    }
    return templateId;
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
      @Parameter(schema = @Schema(
          type = "string",
          format = "binary",
          description = "An Excel (.xlsx) file with a mandatory `.metadata` sheet that contains the CEDAR template ID.",
          requiredMode = Schema.RequiredMode.REQUIRED)) @FormDataParam("input_file") InputStream inputStream,
      @Parameter(hidden = true) @FormDataParam("input_file") FormDataContentDisposition fileDetail) {
    try {
      var workbook = getWorkbook(inputStream);
      var metadataSheet = getMetadataSheet(workbook);
      var cedarTemplateIri = getTemplateIri(metadataSheet);
      var cedarTemplate = cedarService.getCedarTemplateFromIri(cedarTemplateIri);
      var spreadsheetSchema = spreadsheetSchemaGenerator.generateFrom(cedarTemplate);
      var mainSheet = getMainSheet(workbook);
      var spreadsheetData = excelFileHandler.getTableData(mainSheet);
      var spreadsheet = Spreadsheet.create(spreadsheetData);
      var reporting = spreadsheetValidator
          .validate(spreadsheet, spreadsheetSchema)
          .collect(resultCollector);
      var response = ValidateResponse.create(spreadsheetSchema, spreadsheet, reporting);
      return Response.ok(response).build();
    } catch (ValidatorRuntimeException e) {
      return responseErrorMessage(e);
    }
  }

  private XSSFWorkbook getWorkbook(InputStream is) {
    try {
      return excelFileHandler.getWorkbookFromInputStream(is);
    } catch (IOException e) {
      throw new InvalidInputFileException("Invalid metadata Excel file.", e);
    }
  }

  private XSSFSheet getMainSheet(XSSFWorkbook workbook) {
    var sheet = excelFileHandler.getSheet(workbook, "MAIN");
    if (sheet == null) {
      sheet = excelFileHandler.getSheet(workbook, 0);
    }
    if (sheet == null) {
      throw new InvalidInputFileException(
          "Invalid metadata Excel file.",
          new IllegalArgumentException("The [MAIN] sheet is missing."));
    }
    return sheet;
  }

  private XSSFSheet getMetadataSheet(XSSFWorkbook workbook) {
    var sheet = excelFileHandler.getSheet(workbook, ".metadata");
    if (sheet == null) {
      throw new InvalidInputFileException(
          "Invalid metadata Excel file.",
          new IllegalArgumentException("The [.metadata] sheet is missing."));
    }
    return sheet;
  }

  private String getTemplateIri(XSSFSheet metadataSheet) {
    var columnIndex = getDerivedFromColumnLocation(metadataSheet);
    var templateIri = excelFileHandler.getStringCellValue(metadataSheet, 1, columnIndex);
    if (templateIri.trim().isEmpty()) {
      throw new InvalidInputFileException(
          "Invalid metadata Excel file.",
          new IllegalArgumentException("The schema IRI is missing in the [.metadata] sheet."));
    }
    return templateIri;
  }

  private int getDerivedFromColumnLocation(XSSFSheet metadataSheet) {
    var derivedFromColumnLocation = excelFileHandler.findColumnLocation(metadataSheet, "pav:derivedFrom");
    if (!derivedFromColumnLocation.isPresent()) {
      throw new InvalidInputFileException(
          "Invalid metadata Excel file.",
          new IllegalArgumentException("The schema IRI is missing in the [.metadata] sheet."));
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
}
