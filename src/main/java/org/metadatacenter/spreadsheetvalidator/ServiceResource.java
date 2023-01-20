package org.metadatacenter.spreadsheetvalidator;

import com.google.common.collect.ImmutableList;
import io.swagger.v3.oas.annotations.Operation;
import org.metadatacenter.spreadsheetvalidator.domain.Spreadsheet;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetRow;
import org.metadatacenter.spreadsheetvalidator.request.ValidateSpreadsheetRequest;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.singletonMap;

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

  @Inject
  public ServiceResource(@Nonnull CedarService cedarService,
                         @Nonnull SpreadsheetSchemaGenerator spreadsheetSchemaGenerator,
                         @Nonnull SpreadsheetValidator spreadsheetValidator) {
    this.cedarService = checkNotNull(cedarService);
    this.spreadsheetSchemaGenerator = checkNotNull(spreadsheetSchemaGenerator);
    this.spreadsheetValidator = checkNotNull(spreadsheetValidator);
  }

  @POST
  @Operation(summary = "Validate an Excel spreadsheet against a CEDAR template.")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/validate")
  public Response validate(@Nonnull ValidateSpreadsheetRequest request) {
    try {
      var cedarTemplateIri = request.getCedarTemplateIri();
      var cedarTemplate = cedarService.getCedarTemplate(cedarTemplateIri);
      var spreadsheetSchema = spreadsheetSchemaGenerator.generateFrom(cedarTemplate);
      var spreadsheetData = request.getSpreadsheetData();
      var spreadsheet = Spreadsheet.create(spreadsheetData);
      var validationResult = spreadsheetValidator.validate(spreadsheet, spreadsheetSchema);
      return Response.ok(singletonMap("message", "File Uploaded Successfully")).build();
    } catch (Exception e) {
      return Response.serverError().build();
    }
  }
}
