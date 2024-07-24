package org.metadatacenter.spreadsheetvalidator.validator;

import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import javax.annotation.Nonnull;
import java.util.Optional;

import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.STRING;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isString;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.not;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.COLUMN_LABEL;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.VALUE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class StringTypeValidator extends InputValueValidator {

  @Override
  public Optional<ValidationError> validateInputValue(@Nonnull Object value,
                                                      @Nonnull ValueContext valueContext,
                                                      @Nonnull ValidatorContext validatorContext) {
    var columnDescription = valueContext.getColumnDescription();
    var valueType = columnDescription.getColumnType();
    if (valueType == STRING && Assert.that(value, not(isString()))) {
      var validationError = ValidationError.builder()
          .setErrorType("notStringType")
          .setErrorMessage("Value is not a string")
          .setErrorLocation(valueContext.getColumn(), valueContext.getRow())
          .setOtherProp(VALUE, value)
          .setOtherProp(COLUMN_LABEL, columnDescription.getColumnLabel())
          .setOtherProp(SEVERITY, 1)
          .build();
      return Optional.of(validationError);
    }
    return Optional.empty();
  }
}
