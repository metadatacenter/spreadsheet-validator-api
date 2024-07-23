package org.metadatacenter.spreadsheetvalidator.validator;

import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import javax.annotation.Nonnull;

import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.NUMBER;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isNumber;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.not;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ERROR_TYPE;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SUGGESTION;
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
    var columnType = valueContext.getColumnDescription().getColumnType();
    if (columnType == NUMBER && Assert.that(value, not(isNumber()))) {
      var closure = validatorContext.getClosure("numberExtractor");
      var suggestion = closure.execute(value);
      validatorContext.getValidationResult().add(
          ValidationError.builder(valueContext)
              .setErrorDescription("Value is not a number")
              .setProp(VALUE, value)
              .setProp(ERROR_TYPE, "notNumberType")
              .setProp(SUGGESTION, suggestion)
              .setProp(SEVERITY, 1)
              .build()
      );
    }
  }
}
