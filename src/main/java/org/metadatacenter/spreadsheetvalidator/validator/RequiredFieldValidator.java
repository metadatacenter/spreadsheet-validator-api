package org.metadatacenter.spreadsheetvalidator.validator;

import com.google.common.collect.ImmutableList;
import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.Validator;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetRow;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetSchema;
import org.metadatacenter.spreadsheetvalidator.util.Assert;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

import static org.metadatacenter.spreadsheetvalidator.util.Matchers.isNullOrEmpty;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.COLUMN_LABEL;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SEVERITY;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class RequiredFieldValidator implements Validator {

  @Override
  public List<ValidationError> validate(@Nonnull SpreadsheetRow spreadsheetRow, @Nonnull SpreadsheetSchema spreadsheetSchema, @Nonnull ValidatorContext context) {
    return spreadsheetRow.columnStream()
        .filter(spreadsheetSchema::containsColumn)
        .map(columnName -> {
          var columnDescription = spreadsheetSchema.getColumnDescription(columnName);
          var rowIndex = spreadsheetRow.getRowNumber();
          var value = spreadsheetRow.getValue(columnName);
          if (columnDescription.isRequiredColumn() && Assert.that(value, isNullOrEmpty())) {
            var validationError = ValidationError.builder()
                .setErrorType("missingRequired")
                .setErrorMessage("Required value is missing")
                .setErrorLocation(columnName, rowIndex)
                .setOtherProp(COLUMN_LABEL, columnDescription.getColumnLabel())
                .setOtherProp(SEVERITY, 5)
                .build();
            return Optional.of(validationError);
          }
          return Optional.<ValidationError>empty();
        })
        .flatMap(Optional::stream)
        .collect(ImmutableList.toImmutableList());
  }
}
