package org.metadatacenter.spreadsheetvalidator.excel;

import com.google.common.collect.ImmutableMap;
import org.metadatacenter.spreadsheetvalidator.excel.model.ExcelSheetVisitor;
import org.metadatacenter.spreadsheetvalidator.excel.model.ProvenanceKeyword;
import org.metadatacenter.spreadsheetvalidator.excel.model.ProvenanceTable;
import org.metadatacenter.spreadsheetvalidator.request.BadFileException;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.IntStream;

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

    var numberOfDataColumns = headerRow.getPhysicalNumberOfCells();
    var records = IntStream.range(0, numberOfDataColumns)
        .boxed()
        .collect(ImmutableMap.toImmutableMap(
            columnIndex -> excelReader.getStringValue(sheetInstance, 0, columnIndex),
            columnIndex -> excelReader.getValue(sheetInstance, 1, columnIndex)
        ));
    return ProvenanceTable.create(records, provenanceKeyword);
  }
}
