package org.metadatacenter.spreadsheetvalidator.validator;

import org.metadatacenter.spreadsheetvalidator.exception.ValidatorRuntimeException;

import javax.annotation.Nonnull;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class UnexpectedColumnsException extends ValidatorRuntimeException {

  public UnexpectedColumnsException(@Nonnull List<String> invalidColumns) {
    super("Found invalid columns in the input spreadsheet.",
        new IOException("List of invalid columns: " +
            invalidColumns.stream()
                .map(column -> "'" + column + "'")
                .collect(Collectors.joining(", "))),
        Response.Status.BAD_REQUEST.getStatusCode());
  }

  @Override
  public Optional<String> getFixSuggestion() {
    return Optional.of("Please download the latest version of the metadata spreadsheet from the HIVE website.");
  }
}