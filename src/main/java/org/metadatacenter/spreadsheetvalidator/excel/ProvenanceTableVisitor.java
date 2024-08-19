package org.metadatacenter.spreadsheetvalidator.excel;

import com.google.common.collect.Maps;
import org.metadatacenter.spreadsheetvalidator.excel.model.ExcelSheetVisitor;
import org.metadatacenter.spreadsheetvalidator.excel.model.ProvenanceKeyword;
import org.metadatacenter.spreadsheetvalidator.excel.model.ProvenanceTable;
import org.metadatacenter.spreadsheetvalidator.request.BadFileException;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collections;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ProvenanceTableVisitor implements ExcelSheetVisitor<ProvenanceTable> {

  private final ExcelReader excelReader;

  private final ProvenanceKeyword provenanceKeyword;

  private static final String DEFAULT_TABLE_SHEET_NAME = ".metadata";

  @Inject
  public ProvenanceTableVisitor(@Nonnull ExcelReader excelReader,
                                @Nonnull ProvenanceKeyword provenanceKeyword) {
    this.excelReader = checkNotNull(excelReader);
    this.provenanceKeyword = checkNotNull(provenanceKeyword);
  }

  @Override
  public ProvenanceTable visit(ExcelWorkbook workbook) {
    var metadataSheet = workbook.getSheet(DEFAULT_TABLE_SHEET_NAME);
    if (metadataSheet.isEmpty()) {
      throw new BadFileException("Bad Excel file.", new MissingProvenanceSheetException());
    }
    var sheetInstance = metadataSheet.get();
    var headerRow = excelReader.getHeaderRow(sheetInstance);

    var mutableMap = Maps.<String, Object>newHashMap();
    var numberOfDataColumns = headerRow.getPhysicalNumberOfCells();
    for (int columnIndex = 0; columnIndex < numberOfDataColumns; columnIndex++) {
      var key = excelReader.getStringValue(sheetInstance, 0, columnIndex);
      var value = excelReader.getValue(sheetInstance, 1, columnIndex);
      mutableMap.put(key, value);
    }
    var provenanceRecords = Collections.unmodifiableMap(mutableMap);

    return ProvenanceTable.create(provenanceRecords, provenanceKeyword);
  }
}
