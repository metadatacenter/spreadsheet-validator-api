package org.metadatacenter.spreadsheetvalidator.exception;

import jakarta.ws.rs.core.Response;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class MissingRequiredColumnsException extends ValidatorRuntimeException {

  public MissingRequiredColumnsException(@Nonnull List<String> missingColumns) {
    super("Found missing required columns in the input spreadsheet.",
        new IOException("List of missing required columns: " +
            missingColumns.stream()
                .map(column -> "'" + column + "'")
                .collect(Collectors.joining(", "))),
        Response.Status.BAD_REQUEST.getStatusCode());
  }
  @Override
  public Optional<String> getFixSuggestion() {
    return Optional.of("Please download the latest version of the metadata spreadsheet from the HIVE website.");
  }
}
