package org.metadatacenter.spreadsheetvalidator.exception;

import javax.annotation.Nonnull;
import javax.ws.rs.BadRequestException;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public abstract class BadValidatorRequestException extends BadRequestException {

  public BadValidatorRequestException() {
    super();
  }

  public BadValidatorRequestException(@Nonnull String message) {
    super(message);
  }

  @Nonnull
  public abstract Integer getErrorCode();

  @Nonnull
  public abstract String getErrorName();

  @Nonnull
  public abstract String getFixSuggestion();
}
