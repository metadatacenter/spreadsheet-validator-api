package org.metadatacenter.spreadsheetvalidator.inject.module;

import dagger.Module;
import dagger.Provides;
import org.metadatacenter.spreadsheetvalidator.excel.model.DefaultPropertiesKeyword;
import org.metadatacenter.spreadsheetvalidator.excel.model.DefaultProvenanceKeyword;
import org.metadatacenter.spreadsheetvalidator.excel.model.DefaultSchemaKeyword;
import org.metadatacenter.spreadsheetvalidator.excel.model.PropertiesKeyword;
import org.metadatacenter.spreadsheetvalidator.excel.model.ProvenanceKeyword;
import org.metadatacenter.spreadsheetvalidator.excel.model.SchemaKeyword;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class ReservedKeywordModule {

  @Provides
  public PropertiesKeyword providePropertiesKeyword() {
    return new DefaultPropertiesKeyword();
  }

  @Provides
  public SchemaKeyword provideSchemaKeyword() {
    return new DefaultSchemaKeyword();
  }

  @Provides
  public ProvenanceKeyword provenanceKeyword() {
    return new DefaultProvenanceKeyword();
  }
}
