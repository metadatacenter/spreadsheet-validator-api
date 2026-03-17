package org.metadatacenter.spreadsheetvalidator.tsv;

import org.apache.http.HttpStatus;
import org.metadatacenter.spreadsheetvalidator.exception.ValidatorRuntimeException;

import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class MissingMetadataSchemaIdException extends ValidatorRuntimeException {

  public MissingMetadataSchemaIdException(String columnName) {
    super("The metadata record sheet is missing '" + columnName + "' column or its value.", HttpStatus.SC_BAD_REQUEST);
    this.columnName = columnName;
  }

  private final String columnName;

  @Override
  public Optional<String> getFixSuggestion() {
    return Optional.of("Please ensure the metadata record sheet contains the '" + columnName + "' column " +
        "and that it has the correct value.");
  }
}
