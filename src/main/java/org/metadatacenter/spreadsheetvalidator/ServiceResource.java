package org.metadatacenter.spreadsheetvalidator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.metadatacenter.spreadsheetvalidator.domain.Spreadsheet;
import org.metadatacenter.spreadsheetvalidator.exception.ValidatorRuntimeException;
import org.metadatacenter.spreadsheetvalidator.request.ValidateSpreadsheetRequest;
import org.metadatacenter.spreadsheetvalidator.response.ErrorResponse;
import org.metadatacenter.spreadsheetvalidator.response.ValidateResponse;
import org.metadatacenter.spreadsheetvalidator.thirdparty.CedarService;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Path("/service")
@Produces(MediaType.APPLICATION_JSON)
public class ServiceResource {

  private final CedarService cedarService;

  private final SpreadsheetSchemaGenerator spreadsheetSchemaGenerator;

  private final SpreadsheetValidator spreadsheetValidator;

  private final ResultCollector resultCollector;

  @Inject
  public ServiceResource(@Nonnull CedarService cedarService,
                         @Nonnull SpreadsheetSchemaGenerator spreadsheetSchemaGenerator,
                         @Nonnull SpreadsheetValidator spreadsheetValidator,
                         @Nonnull ResultCollector resultCollector) {
    this.cedarService = checkNotNull(cedarService);
    this.spreadsheetSchemaGenerator = checkNotNull(spreadsheetSchemaGenerator);
    this.spreadsheetValidator = checkNotNull(spreadsheetValidator);
    this.resultCollector = checkNotNull(resultCollector);
  }

  @POST
  @Operation(summary = "Validate an Excel spreadsheet against a CEDAR template.")
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
          mediaType = "application/json"
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
      var cedarTemplate = cedarService.getCedarTemplate(cedarTemplateIri);
      var spreadsheetSchema = spreadsheetSchemaGenerator.generateFrom(cedarTemplate);
      var spreadsheetData = request.getSpreadsheetData();
      var spreadsheet = Spreadsheet.create(spreadsheetData);
      var reporting = spreadsheetValidator
          .validate(spreadsheet, spreadsheetSchema)
          .collect(resultCollector);
      var response = ValidateResponse.create(spreadsheetSchema, spreadsheet, reporting);
      return Response.ok(response).build();
    } catch (ValidatorRuntimeException e) {
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
}
