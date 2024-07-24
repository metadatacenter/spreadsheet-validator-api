package org.metadatacenter.spreadsheetvalidator.validator;

import com.google.common.collect.ImmutableList;
import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.Validator;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetRow;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetSchema;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isNullOrEmpty;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.not;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public abstract class InputValueValidator implements Validator {

  @Override
  public List<ValidationError> validate(@Nonnull SpreadsheetRow spreadsheetRow,
                                        @Nonnull SpreadsheetSchema spreadsheetSchema,
                                        @Nonnull ValidatorContext validatorContext) {
    return spreadsheetRow.columnStream()
        .filter(spreadsheetSchema::containsColumn)
        .map(columnName -> {
          var value = spreadsheetRow.getValue(columnName);
          if (Assert.that(value, not(isNullOrEmpty()))) {
            var rowIndex = spreadsheetRow.getRowNumber();
            var columnDescription = spreadsheetSchema.getColumnDescription(columnName);
            var valueContext = ValueContext.create(rowIndex, columnName, columnDescription);
            return validateInputValue(value, valueContext, validatorContext);
          }
          return Optional.<ValidationError>empty();
          })
        .flatMap(Optional::stream)
        .collect(ImmutableList.toImmutableList());
  }

  public abstract Optional<ValidationError> validateInputValue(@Nonnull Object value,
                                                               @Nonnull ValueContext valueContext,
                                                               @Nonnull ValidatorContext validatorContext);
}
