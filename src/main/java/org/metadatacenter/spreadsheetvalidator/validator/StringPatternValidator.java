package org.metadatacenter.spreadsheetvalidator.validator;

import com.google.common.collect.ImmutableMap;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.STRING;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.COLUMN_LABEL;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.COLUMN_NAME;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ERROR_MESSAGE;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ERROR_TYPE;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ROW_INDEX;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.VALUE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class StringPatternValidator extends InputValueValidator {
  @Override
  public void validateInputValue(@Nonnull Object value,
                                 @Nonnull ValueContext valueContext,
                                 @Nonnull ValidatorContext validatorContext) {
    var columnDescription = valueContext.getColumnDescription();
    var valueType = columnDescription.getColumnType();
    if (valueType == STRING && columnDescription.hasRegexString()) {
      var regexString = columnDescription.getRegexString();
      var pattern = Pattern.compile(regexString);
      var matcher = pattern.matcher(String.valueOf(value));
      if (!matcher.matches()) {
        validatorContext.getValidationResultAccumulator().add(
            ImmutableMap.of(
                ROW_INDEX, valueContext.getRow(),
                COLUMN_NAME, valueContext.getColumn(),
                COLUMN_LABEL, columnDescription.getColumnLabel(),
                VALUE, value,
                ERROR_TYPE, "invalidValueFormat",
                ERROR_MESSAGE, "Value does not conform to the expected format",
                SEVERITY, 1
            ));
      }
    }
  }
}
