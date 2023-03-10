package org.metadatacenter.spreadsheetvalidator.validator.exception;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.ws.rs.BadRequestException;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class UnexpectedColumnsException extends BadValidatorRequestException {

  private final List<String> invalidColumns = Lists.newArrayList();

  private final List<String> validColumns = Lists.newArrayList();

  public UnexpectedColumnsException(@Nonnull List<String> invalidColumns,
                                    @Nonnull List<String> validColumns) {
    super();
    this.invalidColumns.addAll(checkNotNull(invalidColumns));
    this.validColumns.addAll(checkNotNull(validColumns));
  }

  @Override
  public Integer getErrorCode() {
    return 1;
  }

  @Override
  public String getErrorName() {
    return "UnexpectedColumnsException";
  }

  @Override
  public String getFixSuggestion() {
    return String.format("Please make sure to use the following column names: %s.", validColumns);
  }

  @Override
  public String getMessage() {
    return String.format("Unexpected column header %s found. ", invalidColumns);
  }
}
