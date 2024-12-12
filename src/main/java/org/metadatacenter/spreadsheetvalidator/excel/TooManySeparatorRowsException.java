package org.metadatacenter.spreadsheetvalidator.excel;

import org.metadatacenter.spreadsheetvalidator.exception.ValidatorRuntimeException;

import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class TooManySeparatorRowsException extends ValidatorRuntimeException {

  public TooManySeparatorRowsException() {
    super("Too many separator rows.");
  }

  @Override
  public Optional<String> getFixSuggestion() {
    return Optional.of("Please ensure your spreadsheet does not contain too many blank rows.");
  }
}
