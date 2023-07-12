package org.metadatacenter.spreadsheetvalidator;

import org.jetbrains.annotations.NotNull;
import org.metadatacenter.spreadsheetvalidator.exception.ValidatorRuntimeException;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class InvalidInputFileException extends ValidatorRuntimeException {

  public InvalidInputFileException(@Nonnull String message,
                                   @Nonnull Throwable cause) {
    super(message, cause, 400);
  }

  @Override
  public Optional<String> getFixSuggestion() {
    return Optional.of("Please download the latest version of the metadata spreadsheet from the HIVE website.");
  }
}
