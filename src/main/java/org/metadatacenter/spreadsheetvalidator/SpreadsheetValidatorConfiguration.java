package org.metadatacenter.spreadsheetvalidator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import io.dropwizard.core.Configuration;
import io.dropwizard.web.conf.WebConfiguration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.metadatacenter.spreadsheetvalidator.thirdparty.BioPortalConfig;
import org.metadatacenter.spreadsheetvalidator.thirdparty.CedarConfig;
import org.metadatacenter.spreadsheetvalidator.thirdparty.ChatGptConfig;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class SpreadsheetValidatorConfiguration extends Configuration {

  private static final String WEB = "web";

  private static final String GENERAL_CONFIG = "general";

  private static final String META_SCHEMA_CONFIG = "metaSchema";

  private static final String CEDAR_CONFIG = "cedarConfig";

  private static final String BIO_PORTAL_CONFIG = "bioPortalConfig";

  private static final String CHAT_GPT_CONFIG = "chatGptConfig";

  private static final String SWAGGER = "swagger";

  @Nonnull
  @JsonCreator
  public static SpreadsheetValidatorConfiguration create(@Nonnull @JsonProperty(WEB) WebConfiguration webConfiguration,
                                                         @Nonnull @JsonProperty(GENERAL_CONFIG) GeneralConfig generalConfig,
                                                         @Nonnull @JsonProperty(META_SCHEMA_CONFIG) MetaSchemaConfig metaSchemaConfig,
                                                         @Nonnull @JsonProperty(CEDAR_CONFIG) CedarConfig cedarConfig,
                                                         @Nonnull @JsonProperty(BIO_PORTAL_CONFIG) BioPortalConfig bioPortalConfig,
                                                         @Nonnull @JsonProperty(CHAT_GPT_CONFIG) ChatGptConfig chatGptConfig,
                                                         @Nonnull @JsonProperty(SWAGGER) SwaggerBundleConfiguration swaggerBundleConfiguration) {
    return new AutoValue_SpreadsheetValidatorConfiguration(webConfiguration, generalConfig, metaSchemaConfig, cedarConfig, bioPortalConfig, chatGptConfig, swaggerBundleConfiguration);
  }

  @Nonnull
  @JsonProperty(WEB)
  public abstract WebConfiguration getWebConfiguration();

  @Nonnull
  @JsonProperty(GENERAL_CONFIG)
  public abstract GeneralConfig getGeneralConfig();

  @Nonnull
  @JsonProperty(META_SCHEMA_CONFIG)
  public abstract MetaSchemaConfig getMetaSchemaConfig();

  @Nonnull
  @JsonProperty(CEDAR_CONFIG)
  public abstract CedarConfig getCedarConfig();

  @Nonnull
  @JsonProperty(BIO_PORTAL_CONFIG)
  public abstract BioPortalConfig getBioPortalConfig();

  @Nonnull
  @JsonProperty(CHAT_GPT_CONFIG)
  public abstract ChatGptConfig getChatGptConfig();

  @Nonnull
  @JsonProperty(SWAGGER)
  public abstract SwaggerBundleConfiguration getSwaggerBundleConfiguration();
}
