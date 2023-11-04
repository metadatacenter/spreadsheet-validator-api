package org.metadatacenter.spreadsheetvalidator;

import jakarta.ws.rs.core.Response;
import org.metadatacenter.spreadsheetvalidator.exception.ValidatorRuntimeException;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class SchemaIdNotFoundException extends ValidatorRuntimeException {

  public SchemaIdNotFoundException(@Nonnull String message,
                                   @Nonnull Throwable cause) {
    super(message, cause, Response.Status.BAD_REQUEST.getStatusCode());
  }

  @Override
  public Optional<String> getFixSuggestion() {
    return Optional.of("Please supply the Metadata Schema ID");
  }
}
