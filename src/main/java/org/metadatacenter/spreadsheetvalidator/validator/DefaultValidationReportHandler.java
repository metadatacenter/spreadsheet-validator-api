package org.metadatacenter.spreadsheetvalidator.validator;

import com.google.common.collect.ImmutableList;
import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidationReport;
import org.metadatacenter.spreadsheetvalidator.ValidationReportHandler;
import org.metadatacenter.spreadsheetvalidator.ValidationReportItem;

import java.util.List;

import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.COLUMN_LABEL;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.COLUMN_NAME;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ERROR_MESSAGE;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ERROR_TYPE;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.ROW_INDEX;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SUGGESTION;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.VALUE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DefaultValidationReportHandler implements ValidationReportHandler {

  @Override
  public ValidationReport of(List<ValidationError> errorList) {
    return ValidationReport.create(errorList.stream()
        .map(validationError ->
            ValidationReportItem.create(
                validationError.getErrorLocation().getRowIndex() + 1,
                (String) validationError.getOtherProp(COLUMN_LABEL),
                validationError.getErrorLocation().getColumnName(),
                validationError.getOtherProp(VALUE),
                validationError.getErrorType(),
                validationError.getErrorMessage(),
                (String) validationError.getOtherProp(SUGGESTION),
                validationError.getErrorLocation().getRowIndex(),
                validationError.getErrorLocation().getColumnName()
            )
        )
        .collect(ImmutableList.toImmutableList()));
  }
}
