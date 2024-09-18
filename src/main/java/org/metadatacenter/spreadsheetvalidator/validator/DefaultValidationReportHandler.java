package org.metadatacenter.spreadsheetvalidator.validator;

import com.google.common.collect.ImmutableList;
import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidationReport;
import org.metadatacenter.spreadsheetvalidator.ValidationReportHandler;
import org.metadatacenter.spreadsheetvalidator.ValidationReportItem;

import java.util.List;

import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.*;

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
                String.valueOf(validationError.getOtherProp(COLUMN_LABEL)),
                validationError.getErrorLocation().getColumnName(),
                validationError.getOtherProp(VALUE),
                validationError.getErrorType(),
                validationError.getErrorMessage(),
                validationError.getOtherProp(SUGGESTION),
                validationError.getErrorLocation().getRowIndex(),
                validationError.getErrorLocation().getColumnName()
            )
        )
        .collect(ImmutableList.toImmutableList()));
  }
}
