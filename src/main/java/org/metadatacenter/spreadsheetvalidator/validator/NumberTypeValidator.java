package org.metadatacenter.spreadsheetvalidator.validator;

import com.google.common.collect.ImmutableMap;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import javax.annotation.Nonnull;

import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.NUMBER;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isNumber;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.not;
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
public class NumberTypeValidator extends InputValueValidator {
  @Override
  public void validateInputValue(@Nonnull Object value,
                                 @Nonnull ValueContext valueContext,
                                 @Nonnull ValidatorContext validatorContext) {
    var columnDescription = valueContext.getColumnDescription();
    var columnType = columnDescription.getColumnType();
    if (columnType == NUMBER && Assert.that(value, not(isNumber()))) {
      var closure = validatorContext.getClosure("numberExtractor");
      var suggestion = closure.execute(value);
      var resultMap = ImmutableMap.of(
          ROW_INDEX, valueContext.getRow(),
          COLUMN_NAME, valueContext.getColumn(),
          COLUMN_LABEL, columnDescription.getColumnLabel(),
          VALUE, value,
          ERROR_TYPE, "notNumberType",
          ERROR_MESSAGE, "Value is not a number",
          SEVERITY, 1
      );
      if (suggestion != null) {
        resultMap.put(ERROR_TYPE, suggestion);
      }
      validatorContext.getValidationResultAccumulator().add(resultMap);
    }
  }
}
