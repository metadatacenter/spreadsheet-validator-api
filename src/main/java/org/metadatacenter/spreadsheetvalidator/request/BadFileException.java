package org.metadatacenter.spreadsheetvalidator.request;

import jakarta.ws.rs.core.Response;
import org.metadatacenter.spreadsheetvalidator.exception.ValidatorRuntimeException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class BadFileException extends ValidatorRuntimeException {

  public BadFileException(@Nonnull String message,
                          @Nonnull Throwable cause) {
    super(message, cause, Response.Status.BAD_REQUEST.getStatusCode());
  }

  @Override
  public Optional<String> getFixSuggestion() {
    var cause = getCause();
    if (cause instanceof IOException){
      return Optional.of("Please download the latest version of the metadata spreadsheet from the HIVE website.");
    } else if (cause instanceof ValidatorRuntimeException) {
      return ((ValidatorRuntimeException) getCause()).getFixSuggestion();
    } else {
      return Optional.empty();
    }
  }
}
