package org.metadatacenter.spreadsheetvalidator.inject.module;

import com.fasterxml.jackson.databind.ObjectReader;
import dagger.Module;
import dagger.Provides;
import org.metadatacenter.spreadsheetvalidator.inject.provider.TsvReaderProvider;
import org.metadatacenter.spreadsheetvalidator.tsv.TsvParser;

import javax.inject.Singleton;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class TsvParserModule {

  @Provides
  @Singleton
  ObjectReader providesObjectReader() {
    return new TsvReaderProvider().get();
  }

  @Provides
  TsvParser providesTsvParser(ObjectReader tsvReader) {
    return new TsvParser(tsvReader);
  }
}
