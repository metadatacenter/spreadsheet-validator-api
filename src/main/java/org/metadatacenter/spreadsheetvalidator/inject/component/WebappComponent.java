package org.metadatacenter.spreadsheetvalidator.inject.component;

import dagger.Component;
import org.metadatacenter.spreadsheetvalidator.ServiceResource;
import org.metadatacenter.spreadsheetvalidator.inject.module.CedarServiceModule;
import org.metadatacenter.spreadsheetvalidator.inject.module.ExcelFileModule;
import org.metadatacenter.spreadsheetvalidator.inject.module.RestServiceModule;
import org.metadatacenter.spreadsheetvalidator.inject.module.SchemaProcessingModule;
import org.metadatacenter.spreadsheetvalidator.inject.module.SpreadsheetValidatorModule;

import javax.inject.Singleton;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Component(modules = {
    CedarServiceModule.class,
    RestServiceModule.class,
    ExcelFileModule.class,
    SchemaProcessingModule.class,
    SpreadsheetValidatorModule.class
})
@Singleton
public interface WebappComponent {

  ServiceResource getServiceResource();
}
