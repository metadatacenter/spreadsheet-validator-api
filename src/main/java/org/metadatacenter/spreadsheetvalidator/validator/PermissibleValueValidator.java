package org.metadatacenter.spreadsheetvalidator.validator;

import com.google.common.collect.ImmutableList;
import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.domain.PermissibleValue;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import javax.annotation.Nonnull;

import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isString;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ERROR_TYPE;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.POSSIBLE_OPTIONS;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SUGGESTION;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isMemberOf;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.not;

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
    if (columnDescription.hasPermissibleValues() && Assert.that(value, isString())) {
      var label = (String) value;
      var permissibleValues = columnDescription.getPermissibleValues();
      var permissibleValueLabels = getPermissibleValueLabels(permissibleValues);
      if (Assert.that(label, not(isMemberOf(permissibleValueLabels)))) {
        var termSuggester = validatorContext.getClosure("termSuggester");
        var suggestion = termSuggester.execute(value, permissibleValues);
        validatorContext.getValidationResult().add(
            ValidationError.builder()
                .setColumnName(valueContext.getColumn())
                .setRowNumber(valueContext.getRow())
                .setInvalidValue(value)
                .setErrorDescription("Value is not part of the permissible values")
                .setProp(POSSIBLE_OPTIONS, permissibleValues.stream().map(PermissibleValue::getLabel))
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
