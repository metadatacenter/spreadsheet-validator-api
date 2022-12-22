package org.metadatacenter.spreadsheetvalidator.algorithm;

import com.google.common.collect.ImmutableList;
import org.metadatacenter.spreadsheetvalidator.RepairClosures;
import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidationResult;
import org.metadatacenter.spreadsheetvalidator.domain.ColumnDescription;
import org.metadatacenter.spreadsheetvalidator.domain.PermissibleValue;
import org.metadatacenter.spreadsheetvalidator.util.ValueAssertion;

import javax.annotation.Nonnull;

import static org.metadatacenter.spreadsheetvalidator.algorithm.PropNames.ERROR_TYPE;
import static org.metadatacenter.spreadsheetvalidator.algorithm.PropNames.POSSIBLE_OPTIONS;
import static org.metadatacenter.spreadsheetvalidator.algorithm.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.algorithm.PropNames.SUGGESTION;
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isMemberOf;

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
      if (ValueAssertion.notEqual(label, isMemberOf(permissibleValueLabels))) {
        var repairClosure = repairClosures.get("autoSuggest");
        var suggestion = repairClosure.execute(value, permissibleValues);
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
