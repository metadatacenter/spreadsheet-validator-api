package org.metadatacenter.spreadsheetvalidator.validator;

import com.google.common.collect.ImmutableList;
import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.domain.PermissibleValue;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import javax.annotation.Nonnull;
import java.util.Optional;

import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isIgnoreCaseMemberOf;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.not;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.COLUMN_LABEL;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SUGGESTION;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.VALUE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class PermissibleValueValidator extends InputValueValidator {

  @Override
  public Optional<ValidationError> validateInputValue(@Nonnull Object value,
                                                      @Nonnull ValueContext valueContext,
                                                      @Nonnull ValidatorContext validatorContext) {
    var columnDescription = valueContext.getColumnDescription();
    if (columnDescription.hasPermissibleValues()) {
      var columnLabel = columnDescription.getColumnLabel();
      var permissibleValues = columnDescription.getPermissibleValues();
      var permissibleValueLabels = getPermissibleValueLabels(permissibleValues);
      if (Assert.that(String.valueOf(value), not(isIgnoreCaseMemberOf(permissibleValueLabels)))) {
        var similarityChecker = validatorContext.getClosure("similarityChecker");
        var suggestion = similarityChecker.execute(columnLabel, value, permissibleValueLabels);
        var validationError = ValidationError.builder()
            .setErrorType("notStandardTerm")
            .setErrorMessage("Value is not among the permissible values")
            .setErrorLocation(valueContext.getColumn(), valueContext.getRow())
            .setOtherProp(VALUE, value)
            .setOtherProp(COLUMN_LABEL, columnLabel)
            .setOtherProp(SEVERITY, 1)
            .setOtherProp(SUGGESTION, suggestion)
            .build();
        return Optional.of(validationError);
      }
    }
    return Optional.empty();
  }

  private ImmutableList<String> getPermissibleValueLabels(ImmutableList<PermissibleValue> permissibleValues) {
    return permissibleValues.stream()
        .map(PermissibleValue::getLabel)
        .collect(ImmutableList.toImmutableList());
  }
}
