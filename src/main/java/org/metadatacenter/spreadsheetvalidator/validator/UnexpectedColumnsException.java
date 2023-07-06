package org.metadatacenter.spreadsheetvalidator.validator;

import com.google.common.collect.Lists;
import org.metadatacenter.spreadsheetvalidator.exception.BadValidatorRequestException;

import javax.annotation.Nonnull;

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
    var sb = new StringBuilder();
    sb.append("1. Please download the latest version of the metadata spreadsheet from CEDAR website.\n\n");
    sb.append(String.format("2. Alternatively, a manual fix can be performed by replacing the unexpect column " +
            "with one of these columns: %s.", validColumns));
    return sb.toString();
  }

  @Override
  public String getMessage() {
    return String.format("Unexpected column header %s found. ", invalidColumns);
  }
}
