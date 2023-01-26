package org.metadatacenter.spreadsheetvalidator;

import com.google.common.collect.Lists;
import org.metadatacenter.spreadsheetvalidator.domain.Spreadsheet;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetSchema;

import javax.annotation.Nonnull;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class SpreadsheetValidator {

  private final ValidatorContext validatorContext;

  private final List<Validator> validatorList = Lists.newArrayList();

  public SpreadsheetValidator(@Nonnull ValidatorContext validatorContext) {
    this.validatorContext = checkNotNull(validatorContext);
  }

  public void setClosure(@Nonnull String key, @Nonnull Closure closure) {
    validatorContext.setClosure(key, closure);
  }

  public void registerValidator(@Nonnull Validator validator) {
    validatorList.add(validator);
  }

  public SpreadsheetValidator validate(Spreadsheet spreadsheet,
                                       SpreadsheetSchema spreadsheetSchema) {
    validatorList.forEach(
        validator -> spreadsheet.getRowStream().forEach(
            spreadsheetRow -> validator.validate(validatorContext, spreadsheetSchema, spreadsheetRow))
    );
    return this;
  }

  public ValidationReport collect(ResultCollector resultCollector) {
    return resultCollector.of(validatorContext.getValidationResult());
  }
}
