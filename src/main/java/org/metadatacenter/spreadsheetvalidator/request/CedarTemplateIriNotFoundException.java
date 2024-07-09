package org.metadatacenter.spreadsheetvalidator.request;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CedarTemplateIriNotFoundException extends ValidatorRequestBodyException {

  public CedarTemplateIriNotFoundException(@Nonnull ValidateSpreadsheetRequest request) {
    super("The required '" + ValidateSpreadsheetRequest.CEDAR_TEMPLATE_IRI + "' is missing from the request body.", request);
  }

  @Override
  public Optional<String> getFixSuggestion() {
    return Optional.of("Please ensure to include '" + ValidateSpreadsheetRequest.CEDAR_TEMPLATE_IRI + "' in the request body.");
  }
}
