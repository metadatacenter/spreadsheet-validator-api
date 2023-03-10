package org.metadatacenter.spreadsheetvalidator.validator.exception;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class BadTemplateException extends BadValidatorRequestException {

  private final String cause;

  public BadTemplateException(@Nonnull String cause) {
    super();
    this.cause = checkNotNull(cause);
  }

  @Override
  public Integer getErrorCode() {
    return 3;
  }

  @Override
  public String getErrorName() {
    return "BadTemplateException";
  }

  @Override
  public String getFixSuggestion() {
    return String.format(
        "Please contact CEDAR (cedar-users@lists.stanford.edu) and send the following text: ", cause);
  }
}
