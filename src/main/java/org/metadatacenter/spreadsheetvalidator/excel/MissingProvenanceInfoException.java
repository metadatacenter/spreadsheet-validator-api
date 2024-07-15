package org.metadatacenter.spreadsheetvalidator.excel;

import org.apache.http.HttpStatus;
import org.metadatacenter.spreadsheetvalidator.exception.ValidatorRuntimeException;

import javax.annotation.Nonnull;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class MissingProvenanceInfoException extends ValidatorRuntimeException {

  private final String columnName;

  public MissingProvenanceInfoException(@Nonnull String columnName) {
    super(format("The provenance sheet is missing '%s' column or its value.", columnName), HttpStatus.SC_BAD_REQUEST);
    this.columnName = checkNotNull(columnName);
  }

  @Override
  public Optional<String> getFixSuggestion() {
    var message = format("Please ensure the provenance sheet contains the '%s' column and that it has " +
        "the correct value.", columnName);
    return Optional.of(message);
  }
}
