package org.metadatacenter.spreadsheetvalidator;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ValidationResultAccumulatorProvider {

  public ValidationResultAccumulator get() {
    return new ValidationResultAccumulator();
  }
}
