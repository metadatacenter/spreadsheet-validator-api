package org.metadatacenter.spreadsheetvalidator;

import com.sun.jersey.multipart.FormDataParam;
import io.swagger.v3.oas.annotations.Operation;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

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

  private final SpreadsheetInputGenerator spreadsheetInputGenerator;

  private final SpreadsheetValidator spreadsheetValidator;

  @Inject
  public ServiceResource(@Nonnull CedarService cedarService,
                         @Nonnull SpreadsheetSchemaGenerator spreadsheetSchemaGenerator,
                         @Nonnull SpreadsheetInputGenerator spreadsheetInputGenerator,
                         @Nonnull SpreadsheetValidator spreadsheetValidator) {
    this.cedarService = checkNotNull(cedarService);
    this.spreadsheetSchemaGenerator = checkNotNull(spreadsheetSchemaGenerator);
    this.spreadsheetInputGenerator = checkNotNull(spreadsheetInputGenerator);
    this.spreadsheetValidator = checkNotNull(spreadsheetValidator);
  }

  @POST
  @Operation(summary = "Validate an Excel spreadsheet against a CEDAR template.")
  @Path("/validate")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response validate(@FormDataParam("file") InputStream inputStream) {
    var cedarTemplate = cedarService.getCedarTemplate("iri");
    var spreadsheetSchema = spreadsheetSchemaGenerator.generateFrom(cedarTemplate);
    var spreadsheetInput = spreadsheetInputGenerator.generateFrom(inputStream);
    var validationResult = spreadsheetValidator.validate(spreadsheetInput, spreadsheetSchema);
    return null;
  }
}
