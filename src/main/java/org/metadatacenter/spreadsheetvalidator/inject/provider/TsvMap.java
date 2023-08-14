package org.metadatacenter.spreadsheetvalidator.inject.provider;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class TsvMap extends HashMap<String, Object> {

  private final CsvSchema schema;
  private final NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);

  public TsvMap(@Nonnull CsvSchema schema) {
    this.schema = schema;
  }

  @Override
  public Object put(String key, Object value) {
    value = convertIfNeeded(value);
    return super.put(key, value);
  }

  private Object convertIfNeeded(Object value) {
    var stringValue = value.toString();
    try {
      var number = new BigDecimal(stringValue);
      try {
        return number.intValueExact();
      } catch (ArithmeticException e1) {
        try {
          return number.longValueExact();
        } catch (ArithmeticException e2) {
          return number.doubleValue();
        }
      }
    } catch (NumberFormatException e) {
      return stringValue;
    }
  }
}
