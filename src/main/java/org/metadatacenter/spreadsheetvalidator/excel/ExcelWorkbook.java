package org.metadatacenter.spreadsheetvalidator.excel;

import org.metadatacenter.spreadsheetvalidator.excel.model.ExcelSheetVisitor;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ExcelWorkbook {

  private final List<CachedSheet> sheetsByIndex;
  private final Map<String, CachedSheet> sheetsByName;

  private ExcelWorkbook(List<CachedSheet> sheetsByIndex, Map<String, CachedSheet> sheetsByName) {
    this.sheetsByIndex = sheetsByIndex;
    this.sheetsByName = sheetsByName;
  }

  @Nonnull
  public static ExcelWorkbook create(List<CachedSheet> sheets) {
    var byName = new LinkedHashMap<String, CachedSheet>();
    for (var sheet : sheets) {
      byName.put(sheet.getName(), sheet);
    }
    return new ExcelWorkbook(List.copyOf(sheets), byName);
  }

  @Nonnull
  public Optional<CachedSheet> getSheet(String sheetName) {
    return Optional.ofNullable(sheetsByName.get(sheetName));
  }

  @Nonnull
  public Optional<CachedSheet> getSheetAt(int sheetIndex) {
    if (sheetIndex >= 0 && sheetIndex < sheetsByIndex.size()) {
      return Optional.of(sheetsByIndex.get(sheetIndex));
    }
    return Optional.empty();
  }

  @Nonnull
  public CachedSheet getFirstSheet() {
    return sheetsByIndex.get(0);
  }

  @Nonnull
  public <T> T accept(ExcelSheetVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
