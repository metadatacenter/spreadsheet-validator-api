package org.metadatacenter.spreadsheetvalidator;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.forms.MultiPartBundle;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import jakarta.servlet.DispatcherType;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.metadatacenter.spreadsheetvalidator.inject.component.DaggerWebappComponent;
import org.metadatacenter.spreadsheetvalidator.inject.module.WebResourceModule;

import java.util.EnumSet;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class SpreadsheetValidatorApplication extends Application<SpreadsheetValidatorConfiguration> {

  public static void main(String[] args) throws Exception {
    new SpreadsheetValidatorApplication().run(args);
  }

  @Override
  public void initialize(final Bootstrap<SpreadsheetValidatorConfiguration> bootstrap) {
    // Swagger initialization
    bootstrap.addBundle(new SwaggerBundle<>() {
      @Override
      protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(SpreadsheetValidatorConfiguration configuration) {
        return configuration.getSwaggerBundleConfiguration();
      }
    });
    bootstrap.addBundle(new MultiPartBundle());
  }

  @Override
  public void run(SpreadsheetValidatorConfiguration spreadsheetValidatorConfiguration,
                  Environment environment) {
    // Enable CORS headers
    final var cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
    cors.setInitParameter("allowedOrigins", "*");
    cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
    cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");
    cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

    var component = DaggerWebappComponent.builder()
        .webResourceModule(new WebResourceModule(spreadsheetValidatorConfiguration))
        .build();
    environment.jersey().register(component.getServiceResource());
  }
}
