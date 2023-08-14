package org.metadatacenter.spreadsheetvalidator.inject.provider;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
class TsvMapInstantiator extends ValueInstantiator.Base {

  private final CsvSchema schema;

  public TsvMapInstantiator(CsvSchema schema) {
    super(TsvMap.class);
    this.schema = schema;
  }

  @Override
  public Object createUsingDefault(DeserializationContext context) {
    return new TsvMap(schema);
  }

  @Override
  public boolean canCreateUsingDefault() {
    return true;
  }
}
