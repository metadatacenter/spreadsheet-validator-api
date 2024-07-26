package org.metadatacenter.spreadsheetvalidator.excel.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.metadatacenter.spreadsheetvalidator.domain.ValueType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DefaultBuiltinTypeMap implements BuiltinTypeMap {

  private static final BuiltinType TEXT = BuiltinType.create("text", ValueType.STRING, ValueType.STRING, null, null);
  private static final BuiltinType DECIMAL = BuiltinType.create("decimal", ValueType.NUMBER, ValueType.DECIMAL, null, "10.50");
  private static final BuiltinType INTEGER = BuiltinType.create("integer", ValueType.NUMBER, ValueType.INTEGER, null, "10");
  private static final BuiltinType FLOAT = BuiltinType.create("float", ValueType.NUMBER, ValueType.DECIMAL, null, "3.14");
  private static final BuiltinType DOUBLE = BuiltinType.create("double", ValueType.NUMBER, ValueType.DECIMAL, null, "3.141592");
  private static final BuiltinType DATE = BuiltinType.create("date", ValueType.DATE, ValueType.DATE, "^\\d{4}-\\d{2}-\\d{2}$", "2024-01-01");
  private static final BuiltinType TIME = BuiltinType.create("time", ValueType.TIME, ValueType.TIME, "^\\d{2}:\\d{2}:\\d{2}$", "13:55:00");
  private static final BuiltinType DATETIME = BuiltinType.create("datetime", ValueType.DATETIME, ValueType.DATETIME, "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:Z|[+-]\\d{2}:\\d{2})?$", "2024-01-01T13:55:00");
  private static final BuiltinType BOOLEAN = BuiltinType.create("boolean", ValueType.BOOLEAN, ValueType.BOOLEAN, null, "true");
  private static final BuiltinType EMAIL = BuiltinType.create("email", ValueType.STRING, ValueType.EMAIL, "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", "john.smith@email.com");
  private static final BuiltinType PHONE = BuiltinType.create("phone", ValueType.STRING, ValueType.PHONE, "^\\+?[0-9. ()-]{10,15}$", "(123) 456-7890");
  private static final BuiltinType URL = BuiltinType.create("url", ValueType.URL, ValueType.URL, null, "https://www.google.com");
  private static final BuiltinType OBO_ID = BuiltinType.create("obo id", ValueType.STRING, ValueType.IDENTIFIER, "^[A-Za-z]+:[0-9]{7}$", "CL:0000000");
  private static final BuiltinType DOI = BuiltinType.create("doi", ValueType.STRING, ValueType.IDENTIFIER, "^10\\.\\d{4,9}/[-._;()/:a-zA-Z0-9]+$", "10.1038/s41597-023-01993-8");
  private static final BuiltinType ORCID = BuiltinType.create("orcid", ValueType.STRING, ValueType.IDENTIFIER, "^\\d{4}-\\d{4}-\\d{4}-\\d{3}[\\dX]$", "0000-0002-2533-6681");
  private static final BuiltinType RRID = BuiltinType.create("rrid", ValueType.STRING, ValueType.IDENTIFIER, "^RRID:[A-Za-z]+_[0-9]+$", "RRID:SCR_005698");
  private static final BuiltinType ROR = BuiltinType.create("ror", ValueType.STRING, ValueType.IDENTIFIER, "^ROR:0[a-hj-km-np-tv-z|0-9]{6}[0-9]{2}$", null);

  private static final Map<String, BuiltinType> BUILTIN_TYPE_MAP = ImmutableMap.<String, BuiltinType>builder()
      .put(TEXT.getName(), TEXT)
      .put(DECIMAL.getName(), DECIMAL)
      .put(INTEGER.getName(), INTEGER)
      .put(FLOAT.getName(), FLOAT)
      .put(DOUBLE.getName(), DOUBLE)
      .put(DATE.getName(), DATE)
      .put(TIME.getName(), TIME)
      .put(DATETIME.getName(), DATETIME)
      .put(BOOLEAN.getName(), BOOLEAN)
      .put(EMAIL.getName(), EMAIL)
      .put(PHONE.getName(), PHONE)
      .put(URL.getName(), URL)
      .put(OBO_ID.getName(), OBO_ID)
      .put(DOI.getName(), DOI)
      .put(ORCID.getName(), ORCID)
      .put(RRID.getName(), RRID)
      .put(ROR.getName(), ROR)
      .build();

  @Override
  @Nonnull
  public BuiltinType getBuiltinType(String name) {
    return BUILTIN_TYPE_MAP.getOrDefault(name, TEXT);
  }

  @Override
  @Nonnull
  public List<BuiltinType> getSupportedTypes() {
    return ImmutableList.copyOf(BUILTIN_TYPE_MAP.values());
  }
}
