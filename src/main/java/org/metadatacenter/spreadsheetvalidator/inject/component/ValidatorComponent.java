package org.metadatacenter.spreadsheetvalidator.inject.component;

import dagger.Component;
import org.metadatacenter.spreadsheetvalidator.SpreadsheetValidator;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Component(modules = {})
public interface ValidatorComponent {
  SpreadsheetValidator getMetadataValidator();
}
