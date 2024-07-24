package org.metadatacenter.spreadsheetvalidator;

import java.util.List;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface ValidationReportHandler {

  ValidationReport of(List<ValidationError> errorList);
}
