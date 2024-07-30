package org.metadatacenter.spreadsheetvalidator.excel;

import com.google.auto.value.AutoValue;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.metadatacenter.spreadsheetvalidator.excel.model.ExcelSheetVisitor;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ExcelWorkbook {

  @Nonnull
  public static ExcelWorkbook create(Workbook workbook) {
    return new AutoValue_ExcelWorkbook(workbook);
  }

  @Nonnull
  public abstract Workbook getWorkbook();

  @Nonnull
  public Optional<Sheet> getSheet(String sheetName) {
    return Optional.ofNullable(getWorkbook().getSheet(sheetName));
  }

  @Nonnull
  public Optional<Sheet> getSheetAt(int sheetIndex) {
    return Optional.ofNullable(getWorkbook().getSheetAt(sheetIndex));
  }

  @Nonnull
  public Sheet getFirstSheet() {
    return getWorkbook().getSheetAt(0);
  }

  @Nonnull
  public <T> T accept(ExcelSheetVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
