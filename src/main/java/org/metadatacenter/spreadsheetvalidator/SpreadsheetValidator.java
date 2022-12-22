package org.metadatacenter.spreadsheetvalidator;

import com.google.auto.value.AutoValue;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetDefinition;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetRow;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class SpreadsheetValidator {

  protected static SpreadsheetValidator create(@Nonnull ValidatorContext validatorContext,
                                               @Nonnull ValidatorElementList validatorElementList) {
    return new AutoValue_SpreadsheetValidator(validatorContext, validatorElementList);
  }

  public static SpreadsheetValidator create(@Nonnull ValidatorContext validatorContext) {
    return create(validatorContext, new ValidatorElementList());
  }

  public static SpreadsheetValidator create(@Nonnull SpreadsheetDefinition spreadsheetDefinition) {
    return create(ValidatorContext.create(spreadsheetDefinition), new ValidatorElementList());
  }

  public abstract ValidatorContext getValidatorContext();

  public abstract ValidatorElementList getValidatorElementList();

  public SpreadsheetValidator onEach(List<SpreadsheetRow> spreadsheetRows, Validator validator) {
    validator.chain(this, spreadsheetRows);
    spreadsheetRows.forEach(spreadsheetRow -> {
          getValidatorElementList().add(
              ValidatorElement.create(validator, spreadsheetRow)
          );
        }
    );
    return this;
  }

  public SpreadsheetValidator validate() {
    getValidatorElementList().stream()
        .forEach(element -> {
          var validator = element.getValidator();
          var spreadsheetRow = element.getSpreadsheetRow();
          validator.validate(getValidatorContext(), spreadsheetRow);
        });
    return this;
  }

  public <T> T collectResult(ResultCollector<T> resultCollector) {
    return resultCollector.of(getValidatorContext().getValidationResult());
  }
}
