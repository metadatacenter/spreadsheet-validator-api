package org.metadatacenter.spreadsheetvalidator.response;

import org.metadatacenter.spreadsheetvalidator.ValidationReport;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface ValidationResponse {

  String getValidationStatus();

  ValidationReport getValidationReport();
}
