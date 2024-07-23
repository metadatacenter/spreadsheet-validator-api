package org.metadatacenter.spreadsheetvalidator.validator;

import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import javax.annotation.Nonnull;

import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.STRING;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isString;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.not;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ERROR_TYPE;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.VALUE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class StringTypeValidator extends InputValueValidator {

  @Override
  public void validateInputValue(@Nonnull Object value,
                                 @Nonnull ValueContext valueContext,
                                 @Nonnull ValidatorContext validatorContext) {
    var valueType = valueContext.getColumnDescription().getColumnType();
    if (valueType == STRING && Assert.that(value, not(isString()))) {
      validatorContext.getValidationResult().add(
          ValidationError.builder(valueContext)
              .setErrorDescription("Value is not a string")
              .setProp(VALUE, value)
              .setProp(ERROR_TYPE, "notStringType")
              .setProp(SEVERITY, 1)
              .build()
      );
    }
  }
}
