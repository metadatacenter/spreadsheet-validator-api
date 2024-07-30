package org.metadatacenter.spreadsheetvalidator.exception;

import jakarta.ws.rs.core.Response;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class MissingRequiredColumnsException extends ValidatorRuntimeException {

  public MissingRequiredColumnsException(@Nonnull String schemaName,
                                         @Nonnull List<String> missingColumns) {
    super("The spreadsheet is missing required fields as specified in the '" + schemaName + "' schema. " +
            "List of missing required columns: " +
            missingColumns.stream()
                .map(column -> "'" + column + "'")
                .collect(Collectors.joining(", ")),
        Response.Status.BAD_REQUEST.getStatusCode());
  }

  @Override
  public Optional<String> getFixSuggestion() {
    return Optional.of("Please make sure the spreadsheet includes all the required columns.");
  }
}
