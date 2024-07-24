package org.metadatacenter.spreadsheetvalidator.validator;

import com.google.common.collect.ImmutableMap;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import javax.annotation.Nonnull;

import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.STRING;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isString;
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
public class StringTypeValidator extends InputValueValidator {

  @Override
  public void validateInputValue(@Nonnull Object value,
                                 @Nonnull ValueContext valueContext,
                                 @Nonnull ValidatorContext validatorContext) {
    var columnDescription = valueContext.getColumnDescription();
    var valueType = columnDescription.getColumnType();
    if (valueType == STRING && Assert.that(value, not(isString()))) {
      validatorContext.getValidationResultAccumulator().add(
          ImmutableMap.of(
              ROW_INDEX, valueContext.getRow(),
              COLUMN_NAME, valueContext.getColumn(),
              COLUMN_LABEL, columnDescription.getColumnLabel(),
              VALUE, value,
              ERROR_TYPE, "notStringType",
              ERROR_MESSAGE, "Value is not a string",
              SEVERITY, 1
          ));
    }
  }
}
