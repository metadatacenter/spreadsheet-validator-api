package org.metadatacenter.spreadsheetvalidator;

import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetSchema;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface SpreadsheetSchemaParser<T> {

  SpreadsheetSchema parse(T input);
}
