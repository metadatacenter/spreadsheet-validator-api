package org.metadatacenter.spreadsheetvalidator.request;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class SpreadsheetDataNotFoundException extends ValidatorRequestBodyException {
  public SpreadsheetDataNotFoundException(@Nonnull String message,
                                          @Nonnull Throwable cause,
                                          @Nonnull ValidateSpreadsheetRequest request) {
    super(message, cause, request);
  }

  @Override
  public Optional<String> getFixSuggestion() {
    return Optional.of("Please ensure to include the spreadsheet data in JSON format in the request body.");
  }
}
