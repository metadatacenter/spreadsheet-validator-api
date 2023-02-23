package org.metadatacenter.spreadsheetvalidator.inject.provider;

import org.metadatacenter.spreadsheetvalidator.RepairClosures;
import org.metadatacenter.spreadsheetvalidator.SpreadsheetValidator;
import org.metadatacenter.spreadsheetvalidator.ValidationResultProvider;
import org.metadatacenter.spreadsheetvalidator.validator.DecimalNumberRangeValidator;
import org.metadatacenter.spreadsheetvalidator.validator.IntegerNumberRangeValidator;
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

  private final RepairClosures repairClosures;

  private final ValidationResultProvider validationResultProvider;

  public SpreadsheetValidatorProvider(@Nonnull RepairClosures repairClosures,
                                      @Nonnull ValidationResultProvider validationResultProvider) {
    this.repairClosures = checkNotNull(repairClosures);
    this.validationResultProvider = checkNotNull(validationResultProvider);
  }

  @Override
  public SpreadsheetValidator get() {
    var validator = new SpreadsheetValidator(repairClosures, validationResultProvider);
    validator.setClosure("numberExtractor", new NumberExtractor());
    validator.setClosure("termSuggester", new TermSuggester());
    validator.registerValidator(new RequiredFieldValidator());
    validator.registerValidator(new StringTypeValidator());
    validator.registerValidator(new NumberTypeValidator());
    validator.registerValidator(new IntegerNumberRangeValidator());
    validator.registerValidator(new DecimalNumberRangeValidator());
    validator.registerValidator(new PermissibleValueValidator());
    return validator;
  }
}
