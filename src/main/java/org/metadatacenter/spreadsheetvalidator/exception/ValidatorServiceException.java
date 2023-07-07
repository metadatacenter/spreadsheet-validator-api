package org.metadatacenter.spreadsheetvalidator.exception;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ValidatorServiceException extends ValidatorRuntimeException {

  public ValidatorServiceException(@Nonnull String message,
                                   @Nonnull Throwable cause,
                                   @Nonnull int statusCode) {
    super(message, cause, statusCode);
  }

  @Override
  public Optional<String> getFixSuggestion() {
    return Optional.empty();
  }
}
