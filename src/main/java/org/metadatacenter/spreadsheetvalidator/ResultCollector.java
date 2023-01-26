package org.metadatacenter.spreadsheetvalidator;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface ResultCollector {

  ValidationReport of(ValidationResult result);
}
