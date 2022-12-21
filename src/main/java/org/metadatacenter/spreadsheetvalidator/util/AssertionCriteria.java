package org.metadatacenter.spreadsheetvalidator.util;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface AssertionCriteria {

  boolean evaluate(Object value);
}
