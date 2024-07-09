package org.metadatacenter.spreadsheetvalidator.request;

import org.metadatacenter.spreadsheetvalidator.exception.ValidatorRuntimeException;

import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class MissingMetadataSchemaIdColumnException extends ValidatorRuntimeException {

  public MissingMetadataSchemaIdColumnException() {
    super("The metadata table is missing the 'metadata_schema_id' column or its values.");
  }

  @Override
  public Optional<String> getFixSuggestion() {
    return Optional.of("Please add a 'metadata_schema_id' column to your spreadsheet and fill it with the necessary values.");
  }
}
