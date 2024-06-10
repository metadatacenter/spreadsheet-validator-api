package org.metadatacenter.spreadsheetvalidator.request;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class SchemaIdNotFoundException extends ValidatorRequestBodyException {

  public SchemaIdNotFoundException(@Nonnull String message,
                                   @Nonnull Throwable cause,
                                   @Nonnull ValidateSpreadsheetRequest request) {
    super(message, cause, request);
  }

  @Override
  public Optional<String> getFixSuggestion() {
    return Optional.of("Please ensure to include the CEDAR template IRI as the schema source in the request body.");
  }
}
