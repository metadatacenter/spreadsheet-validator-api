package org.metadatacenter.spreadsheetvalidator.inject.provider;

import org.metadatacenter.spreadsheetvalidator.SpreadsheetValidator;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.validator.MaxRangeValidator;
import org.metadatacenter.spreadsheetvalidator.validator.MinRangeValidator;
import org.metadatacenter.spreadsheetvalidator.validator.NumberTypeValidator;
import org.metadatacenter.spreadsheetvalidator.validator.PermissibleValueValidator;
import org.metadatacenter.spreadsheetvalidator.validator.RequiredFieldValidator;
import org.metadatacenter.spreadsheetvalidator.validator.StringTypeValidator;
import org.metadatacenter.spreadsheetvalidator.validator.closure.NumberExtractor;
import org.metadatacenter.spreadsheetvalidator.validator.closure.TermSuggester;

import javax.annotation.Nonnull;
import javax.inject.Provider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class SpreadsheetValidatorProvider implements Provider<SpreadsheetValidator> {

  private final ValidatorContext validatorContext;

  public SpreadsheetValidatorProvider(@Nonnull ValidatorContext validatorContext) {
    this.validatorContext = checkNotNull(validatorContext);
  }

  @Override
  public SpreadsheetValidator get() {
    var validator = new SpreadsheetValidator(validatorContext);
    validator.setClosure("numberExtractor", new NumberExtractor());
    validator.setClosure("termSuggester", new TermSuggester());
    validator.registerValidator(new RequiredFieldValidator());
    validator.registerValidator(new StringTypeValidator());
    validator.registerValidator(new NumberTypeValidator());
    validator.registerValidator(new MinRangeValidator());
    validator.registerValidator(new MaxRangeValidator());
    validator.registerValidator(new PermissibleValueValidator());
    return validator;
  }
}
