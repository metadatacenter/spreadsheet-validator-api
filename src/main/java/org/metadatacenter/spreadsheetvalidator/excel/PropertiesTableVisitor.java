package org.metadatacenter.spreadsheetvalidator.excel;

import org.metadatacenter.spreadsheetvalidator.excel.model.ExcelSheetVisitor;
import org.metadatacenter.spreadsheetvalidator.excel.model.PropertiesKeyword;
import org.metadatacenter.spreadsheetvalidator.excel.model.PropertiesTable;
import org.metadatacenter.spreadsheetvalidator.request.BadFileException;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class PropertiesTableVisitor implements ExcelSheetVisitor<PropertiesTable> {

  private final ExcelReader excelReader;

  private final PropertiesKeyword propertiesKeyword;

  private static final String DEFAULT_TABLE_SHEET_NAME = "MAIN";

  @Inject
  public PropertiesTableVisitor(@Nonnull ExcelReader excelReader,
                                @Nonnull PropertiesKeyword propertiesKeyword) {
    this.excelReader = checkNotNull(excelReader);
    this.propertiesKeyword = checkNotNull(propertiesKeyword);
  }

  @Override
  public PropertiesTable visit(ExcelWorkbook workbook) {
    // Table properties are stored in the main sheet
    var dataSheet = workbook.getSheet(DEFAULT_TABLE_SHEET_NAME).orElse(workbook.getFirstSheet());

    // Find the separator rows from the sheet
    var separatorRows = excelReader.findSeparatorRows(dataSheet);
    if (separatorRows.isEmpty()) {
      throw new BadFileException("Bad Excel file", new MissingSeparatorRowException());
    }

    // Locate the properties table when there are actually two separator rows.
    var properties = new Properties();
    if (separatorRows.size() == 2) {
      // The first separator row is dedicated for indicating the properties table
      var separatorIndex = separatorRows.get(0).getRowNum();

      // Extract the properties section from the data sheet.
      var startInfoRowIndex = dataSheet.getTopRow();
      var endInfoRowIndex = separatorIndex - 1;
      var infoRows = excelReader.getRows(dataSheet, startInfoRowIndex, endInfoRowIndex);

      // Collect the rows and store them into the Properties object.
      properties.putAll(infoRows.stream()
          .collect(Collectors.toMap(
              row -> row.getCell(0).getStringCellValue(),   // Assuming key is a string
              row -> row.getCell(1).getStringCellValue()    // Assuming value is a string
          )));
    }
    return PropertiesTable.create(properties, propertiesKeyword);
  }
}
