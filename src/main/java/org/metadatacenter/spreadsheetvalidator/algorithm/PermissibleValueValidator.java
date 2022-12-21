package org.metadatacenter.spreadsheetvalidator.algorithm;

import com.google.common.collect.ImmutableList;
import org.metadatacenter.spreadsheetvalidator.AbstractValidator;
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
import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isString;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class PermissibleValueValidator extends AbstractValidator {

  @Override
  public boolean validateOnEach(@Nonnull Integer rowNumber,
                                @Nonnull String columnName,
                                @Nonnull Object value,
                                @Nonnull ColumnDescription columnDescription,
                                @Nonnull RepairClosures repairClosures,
                                @Nonnull ValidationResult validationResult) {
    if (columnDescription.hasPermissibleValues()) {
      var label = (String) value;
      var permissibleValues = columnDescription.getPermissibleValues();
      if (!isLabelMatched(label, permissibleValues)) {
        var repairClosure = repairClosures.get("autoSuggest");
        var suggestion = repairClosure.execute(value, permissibleValues);
        validationResult.add(
            ValidationError.builder()
                .setRowNumber(rowNumber)
                .setColumnName(columnName)
                .setInvalidValue(value)
                .setErrorDescription("Value is not part of the permissible values")
                .setOtherProp(POSSIBLE_OPTIONS, permissibleValues.stream().map(PermissibleValue::getLabel))
                .setOtherProp(SUGGESTION, suggestion)
                .setOtherProp(ERROR_TYPE, "notStandardTerm")
                .setOtherProp(SEVERITY, 1)
                .build());
      }
    }
    return true;
  }

  private static boolean isLabelMatched(String label, ImmutableList<PermissibleValue> permissibleValues) {
    return permissibleValues.stream()
        .anyMatch(permissibleValue -> permissibleValue.getLabel().equals(label));
  }
}
