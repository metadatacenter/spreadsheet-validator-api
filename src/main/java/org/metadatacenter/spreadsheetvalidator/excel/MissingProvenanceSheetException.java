package org.metadatacenter.spreadsheetvalidator.excel;

import org.metadatacenter.spreadsheetvalidator.exception.ValidatorRuntimeException;

import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class MissingProvenanceSheetException extends ValidatorRuntimeException {

  public MissingProvenanceSheetException() {
    super("The provenance sheet is missing.");
  }

  @Override
  public Optional<String> getFixSuggestion() {
    var docLink = "https://metadatacenter.github.io/spreadsheet-validator-docs/user-manual/upload-spreadsheet.html#how-to-include-the-metadata-information";
    return Optional.of("Please include the '.metadata' sheet. Visit " + docLink + " for more info");
  }
}
