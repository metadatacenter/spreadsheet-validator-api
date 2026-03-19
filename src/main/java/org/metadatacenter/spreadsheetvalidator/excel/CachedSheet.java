package org.metadatacenter.spreadsheetvalidator.excel;

import org.apache.poi.ss.usermodel.Row;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A lightweight wrapper that holds cached row data from a streaming Excel parse,
 * providing indexed random access to rows.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CachedSheet {

  private final String name;
  private final Map<Integer, Row> rows;
  private final int firstRowNum;
  private final int lastRowNum;

  private CachedSheet(@Nonnull String name,
                      @Nonnull Map<Integer, Row> rows,
                      int firstRowNum,
                      int lastRowNum) {
    this.name = name;
    this.rows = rows;
    this.firstRowNum = firstRowNum;
    this.lastRowNum = lastRowNum;
  }

  @Nonnull
  public static CachedSheet create(@Nonnull String name, @Nonnull Map<Integer, Row> rows) {
    if (rows.isEmpty()) {
      return new CachedSheet(name, rows, 0, 0);
    }
    var firstRowNum = rows.keySet().stream().mapToInt(Integer::intValue).min().orElse(0);
    var lastRowNum = rows.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
    return new CachedSheet(name, new LinkedHashMap<>(rows), firstRowNum, lastRowNum);
  }

  @Nonnull
  public String getName() {
    return name;
  }

  @Nullable
  public Row getRow(int rowIndex) {
    return rows.get(rowIndex);
  }

  public int getFirstRowNum() {
    return firstRowNum;
  }

  public int getLastRowNum() {
    return lastRowNum;
  }

  public int getTopRow() {
    return firstRowNum;
  }
}
