package org.metadatacenter.spreadsheetvalidator.request;

import org.apache.http.HttpStatus;
import org.metadatacenter.spreadsheetvalidator.exception.ValidatorRuntimeException;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public abstract class ValidatorRequestBodyException extends ValidatorRuntimeException {

  private final ValidateSpreadsheetRequest request;

  public ValidatorRequestBodyException(@Nonnull String message, @Nonnull ValidateSpreadsheetRequest request) {
    super(message, HttpStatus.SC_BAD_REQUEST);
    this.request = checkNotNull(request);
  }

  public ValidateSpreadsheetRequest getRequest() {
    return request;
  }
}
