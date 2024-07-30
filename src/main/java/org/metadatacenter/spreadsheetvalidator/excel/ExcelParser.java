package org.metadatacenter.spreadsheetvalidator.excel;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.metadatacenter.spreadsheetvalidator.request.BadFileException;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ExcelParser {

  public ExcelWorkbook parse(InputStream inputStream) {
    try {
      var workbook = new XSSFWorkbook(inputStream);
      return ExcelWorkbook.create(workbook);
    } catch (IOException e) {
      throw new BadFileException("Unable to load spreadsheet", e);
    }
  }
}
