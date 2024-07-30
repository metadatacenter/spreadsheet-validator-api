package org.metadatacenter.spreadsheetvalidator.excel.model;

import org.metadatacenter.spreadsheetvalidator.excel.ExcelWorkbook;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface ExcelSheetVisitor<T> {

  T visit(ExcelWorkbook workbook);
}
