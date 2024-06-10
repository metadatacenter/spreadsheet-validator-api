package org.metadatacenter.spreadsheetvalidator.request;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class MissingParameterException extends ValidatorRequestBodyException {

  public MissingParameterException(@Nonnull String parameterKey,
                                   @Nonnull ValidateSpreadsheetRequest request) {
    super(message, cause, request);
  }

  @Override
  public Optional<String> getFixSuggestion() {
    return Optional.empty();
  }
}
