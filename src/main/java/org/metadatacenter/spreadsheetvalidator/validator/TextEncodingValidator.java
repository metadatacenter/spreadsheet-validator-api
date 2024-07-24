package org.metadatacenter.spreadsheetvalidator.validator;

import com.google.common.base.Charsets;
import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;
import java.util.Optional;

import static org.metadatacenter.spreadsheetvalidator.domain.ValueType.STRING;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.COLUMN_LABEL;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.SEVERITY;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.VALUE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class TextEncodingValidator extends InputValueValidator {

  @Override
  public Optional<ValidationError> validateInputValue(@Nonnull Object value,
                                                      @Nonnull ValueContext valueContext,
                                                      @Nonnull ValidatorContext validatorContext) {
    var columnDescription = valueContext.getColumnDescription();
    var valueType = columnDescription.getColumnType();
    var valueEncoding = validatorContext.getValidationSettings().getEncoding();
    if (valueType == STRING && !useValidEncoding(String.valueOf(value), valueEncoding)) {
      var validationError = ValidationError.builder()
          .setErrorType("invalidValueEncoding")
          .setErrorMessage("The value includes non-" + valueEncoding.displayName() + " characters")
          .setErrorLocation(valueContext.getColumn(), valueContext.getRow())
          .setOtherProp(VALUE, value)
          .setOtherProp(COLUMN_LABEL, columnDescription.getColumnLabel())
          .setOtherProp(SEVERITY, 1)
          .build();
      return Optional.of(validationError);
    }
    return Optional.empty();
  }

  private boolean useValidEncoding(String str, Charset encoding) {
    if (encoding.equals(Charsets.US_ASCII)) {
      return isAscii(str);
    } else if (encoding.equals(Charsets.UTF_8)) {
      return isUtf8(str);
    } else if (encoding.equals(Charsets.UTF_16)) {
      return isUtf16(str);
    } else {
      return isUtf8(str);
    }
  }

  private boolean isAscii(String input) {
    for (char ch : input.toCharArray()) {
      if (ch > 127) {
        return false;
      }
    }
    return true;
  }

  private boolean isUtf8(String input) {
    try {
      var bytes = input.getBytes(Charsets.UTF_8);
      var decoded = new String(bytes, Charsets.UTF_8);
      return input.equals(decoded);
    } catch (Exception e) {
      return false;
    }
  }

  private boolean isUtf16(String input) {
    try {
      var bytes = input.getBytes(Charsets.UTF_16);
      var decoded = new String(bytes, Charsets.UTF_16);
      return input.equals(decoded);
    } catch (Exception e) {
      return false;
    }
  }
}
