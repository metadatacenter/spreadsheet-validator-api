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

  private final ValidationResultProvider validationResultProvider;

  private final ValidationSettingsProvider validationSettingsProvider;

  private final List<Validator> validatorList = Lists.newArrayList();

  private ValidatorContext validatorContext;

  private boolean additionalColumnsNotAllowed = false;

  @Inject
  public SpreadsheetValidator(@Nonnull RepairClosures repairClosures,
                              @Nonnull ValidationResultProvider validationResultProvider,
                              @Nonnull ValidationSettingsProvider validationSettingsProvider) {
    this.repairClosures = checkNotNull(repairClosures);
    this.validationResultProvider = checkNotNull(validationResultProvider);
    this.validationSettingsProvider = checkNotNull(validationSettingsProvider);
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
    validatorContext = new ValidatorContext(repairClosures, validationResultProvider.get(), validationSettingsProvider.get());
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
    return resultCollector.of(validatorContext.getValidationResult());
  }
}
