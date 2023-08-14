package org.metadatacenter.spreadsheetvalidator.inject.provider;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import javax.inject.Provider;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class TsvReaderProvider implements Provider<ObjectReader> {

  @Override
  public ObjectReader get() {
    var schema = CsvSchema.emptySchema()
        .withHeader()
        .withColumnSeparator('\t')
        .withLineSeparator("\n");

    // Create schema aware map module
    var tsvMapModule = new SimpleModule();
    tsvMapModule.addValueInstantiator(TsvMap.class, new TsvMapInstantiator(schema));

    var mapper = new CsvMapper();
    mapper.registerModule(tsvMapModule);

    return mapper.readerFor(TsvMap.class).with(schema);
  }
}

