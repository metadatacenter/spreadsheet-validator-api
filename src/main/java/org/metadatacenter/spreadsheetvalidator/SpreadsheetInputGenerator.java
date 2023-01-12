package org.metadatacenter.spreadsheetvalidator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.metadatacenter.spreadsheetvalidator.domain.Spreadsheet;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetRow;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class SpreadsheetInputGenerator {

  private final ObjectMapper objectMapper;

  public SpreadsheetInputGenerator(@Nonnull ObjectMapper objectMapper) {
    this.objectMapper = checkNotNull(objectMapper);
  }

  @Nullable
  public Spreadsheet generateFrom(@Nonnull InputStream inputStream) {
    try {
      var workbook = WorkbookFactory.create(inputStream);
      return generateFromExcel(workbook);
    } catch (IOException e) {
      // TODO: Support CSV/TSV file
    }
    return null;
  }

  @Nonnull
  private Spreadsheet generateFromExcel(Workbook workbook) {
    var sheet = workbook.getSheet("MAIN");
    if (sheet == null) {
      sheet = workbook.getSheetAt(0); // get the first sheet
    }
    var header = sheet.getRow(0); // get the row header
    var listOfRows = toStreamOfRows(sheet)
        .filter(row -> row.getRowNum() != 0)
        .collect(new SpreadsheetRowCollector(header));
    return Spreadsheet.create(listOfRows);
  }

  private Stream<Row> toStreamOfRows(Sheet sheet) {
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(
            sheet.iterator(),
            Spliterator.ORDERED
        ), false);
  }

  class SpreadsheetRowCollector implements Collector<
      Row,
      ImmutableList.Builder<SpreadsheetRow>,
      ImmutableList<SpreadsheetRow>> {

    private final Row header;

    SpreadsheetRowCollector(@Nonnull Row header) {
      this.header = checkNotNull(header);
    }

    @Override
    public Supplier<ImmutableList.Builder<SpreadsheetRow>> supplier() {
      return ImmutableList.Builder::new;
    }

    @Override
    public BiConsumer<ImmutableList.Builder<SpreadsheetRow>, Row> accumulator() {
      return (builder, row) -> {
        var metadataRecord = Maps.<String, Object>newHashMap();
        var rowNumber = row.getRowNum() - 1; // -1 for minus the header row
        for (int i = 0; i < header.getLastCellNum(); i++) {
          var headerCell = header.getCell(i);
          var headerText = headerCell.getStringCellValue();
          var dataCell = row.getCell(i);
          if (dataCell == null) {
            metadataRecord.put(headerText, null);
          } else {
            switch (dataCell.getCellType()) {
              case STRING, FORMULA -> metadataRecord.put(headerText, dataCell.getStringCellValue());
              case NUMERIC -> metadataRecord.put(headerText, dataCell.getNumericCellValue());
              case BOOLEAN -> metadataRecord.put(headerText, dataCell.getBooleanCellValue());
              default -> metadataRecord.put(headerText, null);
            }
          }
        }
        var objectNode = objectMapper.valueToTree(metadataRecord);
        builder.add(SpreadsheetRow.create(rowNumber, metadataRecord));
      };
    }

    @Override
    public BinaryOperator<ImmutableList.Builder<SpreadsheetRow>> combiner() {
      return (builder1, builder2) -> {
        builder1.addAll(builder2.build());
        return builder1;
      };
    }

    @Override
    public Function<ImmutableList.Builder<SpreadsheetRow>, ImmutableList<SpreadsheetRow>> finisher() {
      return ImmutableList.Builder::build;
    }

    @Override
    public Set<Characteristics> characteristics() {
      return ImmutableSet.of();
    }
  }
}
