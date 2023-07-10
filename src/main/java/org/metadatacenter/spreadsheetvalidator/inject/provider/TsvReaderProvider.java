package org.metadatacenter.spreadsheetvalidator.inject.provider;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import javax.inject.Provider;
import java.util.Map;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class TsvReaderProvider implements Provider<ObjectReader> {

  @Override
  public ObjectReader get() {
    var bootstrap = CsvSchema.emptySchema()
        .withHeader()
        .withColumnSeparator('\t')
        .withLineSeparator("\n");
    var mapper = new CsvMapper();
    return mapper.readerFor(Map.class).with(bootstrap);
  }
}

