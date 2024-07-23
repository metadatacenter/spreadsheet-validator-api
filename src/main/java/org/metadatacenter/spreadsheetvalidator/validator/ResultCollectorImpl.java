package org.metadatacenter.spreadsheetvalidator.validator;

import com.google.common.collect.ImmutableList;
import org.metadatacenter.spreadsheetvalidator.ResultCollector;
import org.metadatacenter.spreadsheetvalidator.ValidationReport;
import org.metadatacenter.spreadsheetvalidator.ValidationReportItem;
import org.metadatacenter.spreadsheetvalidator.ValidationResult;

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
public class ResultCollectorImpl implements ResultCollector {

  @Override
  public ValidationReport of(ValidationResult result) {
    return ValidationReport.create(result.stream()
        .map(reportItem ->
            ValidationReportItem.create(
                (Integer) reportItem.get(ROW_INDEX) + 1,
                (String) reportItem.get(COLUMN_LABEL),
                (String) reportItem.get(COLUMN_NAME),
                reportItem.get(VALUE),
                (String) reportItem.get(ERROR_TYPE),
                (String) reportItem.get(ERROR_MESSAGE),
                (String) reportItem.get(SUGGESTION),
                (Integer) reportItem.get(ROW_INDEX),
                (String) reportItem.get(COLUMN_NAME)
            )
        )
        .collect(ImmutableList.toImmutableList()));
  }
}
