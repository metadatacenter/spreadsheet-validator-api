package org.metadatacenter.spreadsheetvalidator;

import com.google.common.base.Charsets;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import java.nio.charset.Charset;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ValidationSettings {

  private final GeneralConfig generalConfig;

  @Inject
  public ValidationSettings(@Nonnull GeneralConfig generalConfig) {
    this.generalConfig = checkNotNull(generalConfig);
  }

  public Charset getEncoding() {
    var encoding = generalConfig.getEncoding();
    switch(encoding) {
      case "ASCII":
        return Charsets.US_ASCII;
      case "UTF-16":
        return Charsets.UTF_16;
      case "UTF-8":
      default:
        return Charsets.UTF_8;
    }
  }

  public String getSchemaColumn() {
    return generalConfig.getSchemaColumn();
  }
}
