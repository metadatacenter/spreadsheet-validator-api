package org.metadatacenter.spreadsheetvalidator.request;

import org.apache.http.HttpStatus;
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
    super(message, cause, HttpStatus.SC_BAD_REQUEST);
  }

  @Override
  public Optional<String> getFixSuggestion() {
    var cause = getCause();
    if (cause instanceof IOException) {
      return Optional.of("Please download the latest version of the metadata spreadsheet from the HIVE website.");
    } else if (cause instanceof ValidatorRuntimeException) {
      return ((ValidatorRuntimeException) getCause()).getFixSuggestion();
    } else {
      return Optional.empty();
    }
  }
}
