package org.metadatacenter.spreadsheetvalidator.excel;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface SpreadsheetModelHandler {

  public Sheet getDataSheet(Workbook workbook);

  public Sheet getProvenanceSheet(Workbook workbook);

  public Sheet getSheet(Workbook workbook, String name);
}
