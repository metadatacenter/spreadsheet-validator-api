package org.metadatacenter.spreadsheetvalidator;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

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

  }
}
