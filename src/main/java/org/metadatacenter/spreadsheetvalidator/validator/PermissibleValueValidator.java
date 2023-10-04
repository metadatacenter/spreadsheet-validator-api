package org.metadatacenter.spreadsheetvalidator.validator;

import com.google.common.collect.ImmutableList;
import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.domain.PermissibleValue;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import javax.annotation.Nonnull;

import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isIgnoreCaseMemberOf;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isString;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.not;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ERROR_TYPE;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SUGGESTION;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.VALUE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class PermissibleValueValidator extends InputValueValidator {
  @Override
  public void validateInputValue(@Nonnull Object value,
                                 @Nonnull ValueContext valueContext,
                                 @Nonnull ValidatorContext validatorContext) {
    var columnDescription = valueContext.getColumnDescription();
    if (columnDescription.hasPermissibleValues()) {
      var label = String.valueOf(value);
      var permissibleValues = columnDescription.getPermissibleValues();
      var permissibleValueLabels = getPermissibleValueLabels(permissibleValues);
      if (Assert.that(label, not(isIgnoreCaseMemberOf(permissibleValueLabels)))) {
        var similarityChecker = validatorContext.getClosure("similarityChecker");
        var suggestion = similarityChecker.execute(value, permissibleValueLabels);
        validatorContext.getValidationResult().add(
            ValidationError.builder()
                .setColumnName(valueContext.getColumn())
                .setRowNumber(valueContext.getRow())
                .setErrorDescription("Value is not part of the permissible values")
                .setProp(VALUE, value)
                .setProp(SUGGESTION, suggestion)
                .setProp(ERROR_TYPE, "notStandardTerm")
                .setProp(SEVERITY, 1)
                .build());
      }
    }
  }

  private ImmutableList<String> getPermissibleValueLabels(ImmutableList<PermissibleValue> permissibleValues) {
    return permissibleValues.stream()
        .map(PermissibleValue::getLabel)
        .collect(ImmutableList.toImmutableList());
  }
}
