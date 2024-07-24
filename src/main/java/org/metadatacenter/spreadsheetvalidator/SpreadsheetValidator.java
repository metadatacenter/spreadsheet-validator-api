package org.metadatacenter.spreadsheetvalidator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.metadatacenter.spreadsheetvalidator.domain.Spreadsheet;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetSchema;
import org.metadatacenter.spreadsheetvalidator.exception.MissingRequiredColumnsException;
import org.metadatacenter.spreadsheetvalidator.exception.UnexpectedColumnsException;
import org.metadatacenter.spreadsheetvalidator.validator.closure.Closure;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class SpreadsheetValidator {

  private final RepairClosures repairClosures;

  private final ValidationSettings validationSettings;

  private final ValidationResultAccumulatorProvider validationResultAccumulatorProvider;

  private final List<Validator> validatorList = Lists.newArrayList();

  private ValidatorContext validatorContext;

  private boolean additionalColumnsNotAllowed = false;

  @Inject
  public SpreadsheetValidator(@Nonnull RepairClosures repairClosures,
                              @Nonnull ValidationSettings validationSettings,
                              @Nonnull ValidationResultAccumulatorProvider validationResultAccumulatorProvider) {
    this.repairClosures = checkNotNull(repairClosures);
    this.validationSettings = checkNotNull(validationSettings);
    this.validationResultAccumulatorProvider = checkNotNull(validationResultAccumulatorProvider);
  }

  public void setClosure(@Nonnull String key, @Nonnull Closure closure) {
    repairClosures.add(key, closure);
  }

  public void registerValidator(@Nonnull Validator validator) {
    validatorList.add(validator);
  }

  public SpreadsheetValidator additionalColumnsNotAllowed() {
    additionalColumnsNotAllowed = true;
    return this;
  }

  public SpreadsheetValidator validate(Spreadsheet spreadsheet,
                                       SpreadsheetSchema spreadsheetSchema) {
    checkAllRequiredFieldsPresent(spreadsheet, spreadsheetSchema);
    if (additionalColumnsNotAllowed) {
      checkAdditionalColumns(spreadsheet, spreadsheetSchema);
    }
    var validationResultAccumulator = validationResultAccumulatorProvider.get();
    validatorContext = new ValidatorContext(repairClosures, validationSettings, validationResultAccumulator);
    spreadsheet.getRowStream().forEach(
        spreadsheetRow -> validatorList.forEach(
            validator -> validator.validate(validatorContext, spreadsheetSchema, spreadsheetRow)));
    return this;
  }

  private static void checkAllRequiredFieldsPresent(Spreadsheet spreadsheet,
                                             SpreadsheetSchema spreadsheetSchema) {
    var spreadsheetColumns = spreadsheet.getColumns();
    var missingColumns = spreadsheetSchema.getRequiredColumns()
        .stream()
        .filter(column -> !spreadsheetColumns.contains(column))
        .collect(ImmutableList.toImmutableList());
    if (!missingColumns.isEmpty()) {
      var schemaName = spreadsheetSchema.getName();
      throw new MissingRequiredColumnsException(schemaName, missingColumns);
    }
  }

  private static void checkAdditionalColumns(Spreadsheet spreadsheet, SpreadsheetSchema schema) {
    var additionalColumns = Lists.<String>newArrayList();
    spreadsheet.getColumns().stream()
        .forEach(column -> {
          if (!schema.containsColumn(column)) {
            additionalColumns.add(column);
          }
        });
    if (!additionalColumns.isEmpty()) {
      throw new UnexpectedColumnsException(additionalColumns);
    }
  }

  public ValidationReport collect(ResultCollector resultCollector) {
    var validationResultCollector = validatorContext.getValidationResultAccumulator();
    return resultCollector.of(validationResultCollector.toValidationResult());
  }
}
