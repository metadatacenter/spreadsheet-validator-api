package org.metadatacenter.spreadsheetvalidator.inject.component;

import dagger.Component;
import org.metadatacenter.spreadsheetvalidator.CedarConfig;
import org.metadatacenter.spreadsheetvalidator.inject.module.WebResourceModule;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Component(modules = {WebResourceModule.class})
public interface WebappComponent {
  CedarConfig getCedarConfig();
}
