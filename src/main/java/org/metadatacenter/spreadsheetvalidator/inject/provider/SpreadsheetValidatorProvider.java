package org.metadatacenter.spreadsheetvalidator.inject.provider;

import org.metadatacenter.spreadsheetvalidator.RepairClosures;
import org.metadatacenter.spreadsheetvalidator.SpreadsheetValidator;
import org.metadatacenter.spreadsheetvalidator.ValidationResultProvider;
import org.metadatacenter.spreadsheetvalidator.thirdparty.ChatGptService;
import org.metadatacenter.spreadsheetvalidator.validator.DecimalNumberRangeValidator;
import org.metadatacenter.spreadsheetvalidator.validator.IntegerNumberRangeValidator;
import org.metadatacenter.spreadsheetvalidator.validator.NumberTypeValidator;
import org.metadatacenter.spreadsheetvalidator.validator.PermissibleValueValidator;
import org.metadatacenter.spreadsheetvalidator.validator.RequiredFieldValidator;
import org.metadatacenter.spreadsheetvalidator.validator.StringPatternValidator;
import org.metadatacenter.spreadsheetvalidator.validator.UrlValidator;
import org.metadatacenter.spreadsheetvalidator.validator.closure.ChatGptSimilarityChecker;
import org.metadatacenter.spreadsheetvalidator.validator.closure.NumberExtractor;
import org.metadatacenter.spreadsheetvalidator.validator.closure.SimpleSimilarityChecker;

import javax.annotation.Nonnull;
import javax.inject.Provider;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Singleton
public class SpreadsheetValidatorProvider implements Provider<SpreadsheetValidator> {

  private final RepairClosures repairClosures;

  private final ValidationResultProvider validationResultProvider;

  private final ChatGptService chatGptService;

  public SpreadsheetValidatorProvider(@Nonnull RepairClosures repairClosures,
                                      @Nonnull ValidationResultProvider validationResultProvider,
                                      @Nonnull ChatGptService chatGptService) {
    this.repairClosures = checkNotNull(repairClosures);
    this.validationResultProvider = checkNotNull(validationResultProvider);
    this.chatGptService = checkNotNull(chatGptService);
  }

  @Override
  public SpreadsheetValidator get() {
    var validator = new SpreadsheetValidator(repairClosures, validationResultProvider);
    validator.setClosure("numberExtractor", new NumberExtractor());
    validator.setClosure("similarityChecker", new ChatGptSimilarityChecker(chatGptService, new SimpleSimilarityChecker()));
    validator.registerValidator(new RequiredFieldValidator());
    validator.registerValidator(new NumberTypeValidator());
    validator.registerValidator(new IntegerNumberRangeValidator());
    validator.registerValidator(new DecimalNumberRangeValidator());
    validator.registerValidator(new PermissibleValueValidator());
    validator.registerValidator(new StringPatternValidator());
    validator.registerValidator(new UrlValidator());
    return validator;
  }
}
