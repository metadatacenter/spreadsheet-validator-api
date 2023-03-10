package org.metadatacenter.spreadsheetvalidator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import in.vectorpro.dropwizard.swagger.SwaggerBundleConfiguration;
import io.dropwizard.Configuration;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class SpreadsheetValidatorConfiguration extends Configuration {

  private static final String CEDAR_CONFIG = "cedarConfig";

  private static final String SWAGGER = "swagger";

  @Nonnull
  @JsonCreator
  public static SpreadsheetValidatorConfiguration create(@Nonnull @JsonProperty(CEDAR_CONFIG) CedarConfig cedarConfig,
                                                         @Nonnull @JsonProperty(SWAGGER) SwaggerBundleConfiguration swaggerBundleConfiguration) {
    return new AutoValue_SpreadsheetValidatorConfiguration(cedarConfig, swaggerBundleConfiguration);
  }

  @Nonnull
  @JsonProperty(CEDAR_CONFIG)
  public abstract CedarConfig getCedarConfig();

  @Nonnull
  @JsonProperty(SWAGGER)
  public abstract SwaggerBundleConfiguration getSwaggerBundleConfiguration();
}
