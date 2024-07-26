package org.metadatacenter.spreadsheetvalidator.tsv;

import autovalue.shaded.com.google.common.collect.ImmutableList;
import com.fasterxml.jackson.databind.ObjectReader;
import org.metadatacenter.spreadsheetvalidator.request.BadFileException;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class TsvParser {

  private final ObjectReader tsvReader;

  @Inject
  public TsvParser(@Nonnull ObjectReader tsvReader) {
    this.tsvReader = checkNotNull(tsvReader);
  }

  @Nonnull
  public List<Map<String, Object>> parse(InputStream inputStream) {
    try {
      var dataString = readInputStream(inputStream);
      return tsvReader.readValues(dataString).readAll().stream()
          .map(item -> (Map<String, Object>) item)
          .collect(ImmutableList.toImmutableList());
    } catch (IOException e) {
      throw new BadFileException("Bad TSV file.", e);
    }
  }

  @Nonnull
  private String readInputStream(InputStream inputStream) throws IOException {
    return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
  }
}
