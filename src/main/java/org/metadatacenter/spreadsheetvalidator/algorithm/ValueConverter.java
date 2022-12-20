package org.metadatacenter.spreadsheetvalidator.algorithm;

import org.metadatacenter.spreadsheetvalidator.domain.PermissibleValue;

import javax.annotation.Nonnull;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ValueConverter {

  private final ConvertToNumberHandler convertToNumberHandler;
  private final ConvertToStringHandler convertToStringHandler;
  private final ConvertToPermissibleValueHandler convertToPermissibleValueHandler;

  public ValueConverter(@Nonnull ConvertToNumberHandler convertToNumberHandler,
                        @Nonnull ConvertToStringHandler convertToStringHandler,
                        @Nonnull ConvertToPermissibleValueHandler convertToPermissibleValueHandler) {
    this.convertToNumberHandler = checkNotNull(convertToNumberHandler);
    this.convertToStringHandler = checkNotNull(convertToStringHandler);
    this.convertToPermissibleValueHandler = checkNotNull(convertToPermissibleValueHandler);
  }

  public Integer toInteger(Object targetValue) {
    return null; //convertToNumberHandler.execute(targetValue);
  }

  public String toDecimal(Object targetValue, String decimalPattern) {
    return null; //convertToNumberHandler.execute(targetValue, decimalPattern);
  }

  public String toString(Object targetValue, String stringPattern) {
    return null; //convertToStringHandler.execute(targetValue, stringPattern);
  }

  public String toPermissibleValue(Object targetValue, List<PermissibleValue> permissibleValueList) {
    return null; //convertToPermissibleValueHandler.execute(targetValue, permissibleValueList);
  }
}
