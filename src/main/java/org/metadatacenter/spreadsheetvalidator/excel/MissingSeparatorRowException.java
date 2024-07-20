package org.metadatacenter.spreadsheetvalidator.excel;

import org.metadatacenter.spreadsheetvalidator.exception.ValidatorRuntimeException;

import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class MissingSeparatorRowException extends ValidatorRuntimeException {

  public MissingSeparatorRowException() {
    super("Missing a separator row.");
  }

  @Override
  public Optional<String> getFixSuggestion() {
    return Optional.of("Please add a new blank row between the schema table and the data table to separate.");
  }
}
