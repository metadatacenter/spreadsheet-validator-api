package org.metadatacenter.spreadsheetvalidator.validator;

import com.google.common.collect.ImmutableList;
import org.metadatacenter.spreadsheetvalidator.RepairClosures;
import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidationResult;
import org.metadatacenter.spreadsheetvalidator.domain.ColumnDescription;
import org.metadatacenter.spreadsheetvalidator.domain.PermissibleValue;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import javax.annotation.Nonnull;

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
                                 @Nonnull String columnName,
                                 @Nonnull Integer rowNumber,
                                 @Nonnull ColumnDescription columnDescription,
                                 @Nonnull RepairClosures repairClosures,
                                 @Nonnull ValidationResult validationResult) {
    if (columnDescription.hasPermissibleValues()) {
      var label = (String) value;
      var permissibleValues = columnDescription.getPermissibleValues();
      var permissibleValueLabels = getPermissibleValueLabels(permissibleValues);
      if (Assert.that(label, not(isMemberOf(permissibleValueLabels)))) {
        var termSuggester = repairClosures.get("termSuggester");
        var suggestion = termSuggester.execute(value, permissibleValues);
        validationResult.add(
            ValidationError.builder()
                .setColumnName(columnName)
                .setRowNumber(rowNumber)
                .setInvalidValue(value)
                .setErrorDescription("Value is not part of the permissible values")
                .setOtherProp(POSSIBLE_OPTIONS, permissibleValues.stream().map(PermissibleValue::getLabel))
                .setOtherProp(SUGGESTION, suggestion)
                .setOtherProp(ERROR_TYPE, "notStandardTerm")
                .setOtherProp(SEVERITY, 1)
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
