package org.metadatacenter.spreadsheetvalidator.request;

import org.metadatacenter.spreadsheetvalidator.exception.ValidatorRuntimeException;

import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class MissingDerivedFromColumnException extends ValidatorRuntimeException {

  public MissingDerivedFromColumnException() {
    super("The .metadata sheet is missing 'pav:derivedFrom' column or its value.");
  }

  @Override
  public Optional<String> getFixSuggestion() {
    return Optional.of("Please include 'pav:derivedFrom' in the .metadata sheet in your Excel file.");
  }
}
