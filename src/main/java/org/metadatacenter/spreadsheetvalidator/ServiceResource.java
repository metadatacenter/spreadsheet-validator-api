package org.metadatacenter.spreadsheetvalidator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.swagger.v3.oas.annotations.Operation;

import javax.annotation.Nonnull;
import javax.inject.Inject;
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
  private final SpreadsheetValidator spreadsheetValidator;

  @Inject
  public ServiceResource(@Nonnull CedarService cedarService,
                         @Nonnull SpreadsheetValidator spreadsheetValidator) {
    this.cedarService = checkNotNull(cedarService);
    this.spreadsheetValidator = checkNotNull(spreadsheetValidator);
  }

  @POST
  @Operation(summary = "Validate a collection of metadata against a CEDAR template.")
  @Path("/validate")
  public Response validate(ImmutableList<ImmutableMap<String, Object>> metadataCollection,
                           String cedarTemplateIri) {
//    var validatorComponent = DaggerValidatorComponent.builder()
//        .metadataSpecificationModule(new Me)
//        .builder();
//    var metadataValidator = validatorComponent.getMetadataValidator();
//    return metadataValidator.validate(metadataCollection);
    return null;
  }
}
