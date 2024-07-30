package org.metadatacenter.spreadsheetvalidator.excel;

import org.apache.http.HttpStatus;
import org.metadatacenter.spreadsheetvalidator.exception.ValidatorRuntimeException;

import javax.annotation.Nonnull;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class MissingProvenanceTemplateIri extends ValidatorRuntimeException {

  public MissingProvenanceTemplateIri() {
    super(format("The provenance sheet is missing the template IRI information required to link the data sheet " +
        "to its predefined schema."), HttpStatus.SC_BAD_REQUEST);
  }

  @Override
  public Optional<String> getFixSuggestion() {
    var message = format("Please ensure the template IRI linking to the schema resource is included in the provenance sheet.");
    return Optional.of(message);
  }
}
