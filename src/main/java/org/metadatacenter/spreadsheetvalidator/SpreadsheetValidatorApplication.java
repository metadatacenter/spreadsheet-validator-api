package org.metadatacenter.spreadsheetvalidator;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.metadatacenter.spreadsheetvalidator.inject.component.DaggerWebappComponent;
import org.metadatacenter.spreadsheetvalidator.inject.module.WebResourceModule;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class SpreadsheetValidatorApplication extends Application<SpreadsheetValidatorConfiguration> {

  public static void main(String[] args) throws Exception {
    new SpreadsheetValidatorApplication().run(args);
  }

  @Override
  public void run(SpreadsheetValidatorConfiguration spreadsheetValidatorConfiguration,
                  Environment environment) throws Exception {
    var component = DaggerWebappComponent.builder()
        .webResourceModule(new WebResourceModule(spreadsheetValidatorConfiguration))
        .build();
    environment.jersey().register(component.getServiceResource());
  }
}
