package org.metadatacenter.spreadsheetvalidator.excel.model;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Properties;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class PropertiesTable {

  @Nonnull
  public static PropertiesTable create(@Nonnull Properties properties,
                                       @Nonnull PropertiesKeyword propertiesKeyword) {
    return new AutoValue_PropertiesTable(properties, propertiesKeyword);
  }

  @Nonnull
  protected abstract Properties getProperties();

  @Nonnull
  protected abstract PropertiesKeyword getPropertiesKeyword();

  @Nonnull
  public Optional<String> getTitle() {
    return Optional.ofNullable((String) getProperties().get(getPropertiesKeyword().ofTitle()));
  }

  @Nonnull
  public Optional<String> getAuthors() {
    return Optional.ofNullable((String) getProperties().get(getPropertiesKeyword().ofAuthors()));
  }

  @Nonnull
  public Optional<String> getMetaSchemaId() {
    return Optional.ofNullable((String) getProperties().get(getPropertiesKeyword().ofMetaSchemaId()));
  }

  @Nonnull
  public Optional<String> getVersion() {
    return Optional.ofNullable((String) getProperties().get(getPropertiesKeyword().ofVersion()));
  }
}
