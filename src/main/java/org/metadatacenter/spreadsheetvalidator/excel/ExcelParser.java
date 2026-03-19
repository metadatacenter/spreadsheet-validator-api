package org.metadatacenter.spreadsheetvalidator.excel;

import com.github.pjfanning.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.metadatacenter.spreadsheetvalidator.request.BadFileException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ExcelParser {

  public ExcelWorkbook parse(InputStream inputStream) {
    try (var workbook = StreamingReader.builder()
        .rowCacheSize(100)
        .bufferSize(4096)
        .open(inputStream)) {
      return cacheWorkbook(workbook);
    } catch (IOException e) {
      throw new BadFileException("Unable to load spreadsheet", e);
    }
  }

  private ExcelWorkbook cacheWorkbook(Workbook workbook) {
    var cachedSheets = new ArrayList<CachedSheet>();
    for (var sheet : workbook) {
      cachedSheets.add(cacheSheet(sheet));
    }
    return ExcelWorkbook.create(cachedSheets);
  }

  private CachedSheet cacheSheet(Sheet sheet) {
    var rows = new LinkedHashMap<Integer, Row>();
    for (var row : sheet) {
      rows.put(row.getRowNum(), row);
    }
    return CachedSheet.create(sheet.getSheetName(), rows);
  }
}
