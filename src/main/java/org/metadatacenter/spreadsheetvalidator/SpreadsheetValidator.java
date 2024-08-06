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

  private final List<Validator> validators = Lists.newArrayList();

  private List<ValidationError> validationErrors = Lists.newArrayList();

  private boolean additionalColumnsNotAllowed = false;

  @Inject
  public SpreadsheetValidator(@Nonnull RepairClosures repairClosures,
                              @Nonnull ValidationSettings validationSettings) {
    this.repairClosures = checkNotNull(repairClosures);
    this.validationSettings = checkNotNull(validationSettings);
  }

  public void setClosure(@Nonnull String key, @Nonnull Closure closure) {
    repairClosures.add(key, closure);
  }

  public void registerValidator(@Nonnull Validator validator) {
    validators.add(validator);
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

    var validatorContext = new ValidatorContext(repairClosures, validationSettings);
    validationErrors = spreadsheet.getRowStream()
        .flatMap(spreadsheetRow -> validators.stream()
            .flatMap(validator -> validator.validate(spreadsheetRow, spreadsheetSchema, validatorContext).stream()))
        .collect(ImmutableList.toImmutableList());

    return this;
  }

  private static void checkAllRequiredFieldsPresent(Spreadsheet spreadsheet, SpreadsheetSchema spreadsheetSchema) {
    var spreadsheetColumns = spreadsheet.getColumns();
    var unfoldedSchema = spreadsheetSchema.unfold();
    var missingColumns = spreadsheetSchema.getRequiredColumns()
        .stream()
        .filter(column -> {
          var columnDescription = unfoldedSchema.getColumnDescription(column);
          var columnName = columnDescription.getColumnName();
          var columnLabel = columnDescription.getColumnLabel();
          return !(spreadsheetColumns.contains(columnName) || spreadsheetColumns.contains(columnLabel));
        })
        .collect(ImmutableList.toImmutableList());
    if (!missingColumns.isEmpty()) {
      var schemaName = spreadsheetSchema.getName();
      throw new MissingRequiredColumnsException(schemaName, missingColumns);
    }
  }

  private static void checkAdditionalColumns(Spreadsheet spreadsheet, SpreadsheetSchema schema) {
    var additionalColumns = Lists.<String>newArrayList();
    var unfoldedSchema = schema.unfold();
    spreadsheet.getColumns()
        .forEach(column -> {
          if (!unfoldedSchema.containsColumn(column)) {
            additionalColumns.add(column);
          }
        });
    if (!additionalColumns.isEmpty()) {
      throw new UnexpectedColumnsException(additionalColumns);
    }
  }

  public ValidationReport collect(ValidationReportHandler validationReportHandler) {
    return validationReportHandler.of(validationErrors);
  }
}
