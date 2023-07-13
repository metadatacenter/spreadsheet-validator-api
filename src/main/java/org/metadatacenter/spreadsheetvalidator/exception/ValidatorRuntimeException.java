package org.metadatacenter.spreadsheetvalidator.exception;

import jakarta.ws.rs.WebApplicationException;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public abstract class ValidatorRuntimeException extends WebApplicationException {

  public ValidatorRuntimeException(@Nonnull String message,
                                   @Nonnull Throwable cause,
                                   @Nonnull int statusCode) {
    super(message, cause, statusCode);
  }

  public abstract Optional<String> getFixSuggestion();
}
