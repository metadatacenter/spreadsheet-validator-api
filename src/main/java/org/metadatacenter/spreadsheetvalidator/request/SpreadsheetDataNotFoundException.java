package org.metadatacenter.spreadsheetvalidator.request;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class SpreadsheetDataNotFoundException extends ValidatorRequestBodyException {

  public SpreadsheetDataNotFoundException(@Nonnull ValidateSpreadsheetRequest request) {
    super("The required '" + ValidateSpreadsheetRequest.SPREADSHEET_DATA + "' is missing from the request body.", request);
  }

  @Override
  public Optional<String> getFixSuggestion() {
    return Optional.of("Please ensure to include '" + ValidateSpreadsheetRequest.SPREADSHEET_DATA + "' in the request body.");
  }
}
