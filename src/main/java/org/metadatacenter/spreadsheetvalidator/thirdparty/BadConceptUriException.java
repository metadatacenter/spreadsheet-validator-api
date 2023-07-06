package org.metadatacenter.spreadsheetvalidator.thirdparty;

import org.jetbrains.annotations.NotNull;
import org.metadatacenter.spreadsheetvalidator.exception.BadValidatorRequestException;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class BadConceptUriException extends BadValidatorRequestException {

  private final String cause;

  public BadConceptUriException(@Nonnull String cause) {
    super();
    this.cause = checkNotNull(cause);
  }

  @NotNull
  @Override
  public Integer getErrorCode() {
    return 3;
  }

  @NotNull
  @Override
  public String getErrorName() {
    return "BadConceptURIException";
  }

  @NotNull
  @Override
  public String getFixSuggestion() {
    return String.format(
        "Please contact BioPortal admin (support@bioontology.org) and send the following error message: ", cause);
  }
}
