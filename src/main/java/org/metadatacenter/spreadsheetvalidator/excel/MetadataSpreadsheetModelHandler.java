package org.metadatacenter.spreadsheetvalidator.excel;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.metadatacenter.spreadsheetvalidator.request.BadFileException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class MetadataSpreadsheetModelHandler implements SpreadsheetModelHandler {

  private static final String DATA_SHEET_NAME = "MAIN";
  private static final String PROVENANCE_SHEET_NAME = ".metadata";

  @Override
  @Nonnull
  public Sheet getDataSheet(Workbook workbook) {
    var sheet = getSheet(workbook, DATA_SHEET_NAME); // Look for a sheet used as the default
    if (sheet == null) {
      sheet = workbook.getSheetAt(0); // Otherwise, the data sheet is always the first sheet.
    }
    return sheet;
  }

  @Override
  @Nonnull
  public Sheet getProvenanceSheet(Workbook workbook) {
    var sheet = getSheet(workbook, PROVENANCE_SHEET_NAME);
    if (sheet == null) {
      throw new BadFileException("Bad Excel file.", new MissingProvenanceSheetException());
    }
    return sheet;
  }

  @Override
  @Nullable
  public Sheet getSheet(Workbook workbook, String name) {
    return workbook.getSheet(name);
  }
}
