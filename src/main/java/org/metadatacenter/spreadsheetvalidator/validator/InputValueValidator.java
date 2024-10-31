package org.metadatacenter.spreadsheetvalidator.validator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.Validator;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetRow;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetSchema;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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
    var unfoldedSchema = spreadsheetSchema.unfold();
    return spreadsheetRow.columnStream()
        .filter(unfoldedSchema::containsColumn)
        .flatMap(columnName -> {
          var value = spreadsheetRow.getValue(columnName);
          if (Assert.that(value, not(isNullOrEmpty()))) {
            var rowIndex = spreadsheetRow.getRowNumber();
            var columnDescription = unfoldedSchema.getColumnDescription(columnName);
            var valueContext = ValueContext.create(rowIndex, columnName, columnDescription);
            if (columnDescription.isMultiValued()) {
              return splitByCommaIgnoringQuotes(String.valueOf(value)).stream()
                .map(v -> validateInputValue(v, valueContext, validatorContext))
                .flatMap(Optional::stream);
            } else {
              return validateInputValue(value, valueContext, validatorContext).stream();
            }
          }
          return Stream.empty();
        })
        .collect(ImmutableList.toImmutableList());
  }

  private static List<String> splitByCommaIgnoringQuotes(String input) {
    // Regex to match quoted text or text outside quotes
    var regex = "\"([^\"]*)\"|([^,]+)";

    var pattern = Pattern.compile(regex);
    var matcher = pattern.matcher(input);

    var result = Lists.<String>newArrayList();
    while (matcher.find()) {
      if (matcher.group(1) != null) {
        // If group 1 matched, it means a quoted string was found
        result.add(matcher.group(1));
      } else {
        // If group 2 matched, it's a text outside quotes
        result.add(matcher.group(2).trim());
      }
    }
    return result;
  }

  public abstract Optional<ValidationError> validateInputValue(@Nonnull Object value,
                                                               @Nonnull ValueContext valueContext,
                                                               @Nonnull ValidatorContext validatorContext);
}
