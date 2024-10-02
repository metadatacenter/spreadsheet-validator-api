package org.metadatacenter.spreadsheetvalidator.validator;

import org.metadatacenter.spreadsheetvalidator.ValidationError;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.thirdparty.CedarService;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.metadatacenter.spreadsheetvalidator.validator.PropNames.*;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class MetadataSchemaValidator extends InputValueValidator {

  private final CedarService cedarService;

  public MetadataSchemaValidator(@Nonnull CedarService cedarService) {
    this.cedarService = checkNotNull(cedarService);
  }

  @Override
  public void validateInputValue(@Nonnull Object value,
                                 @Nonnull ValueContext valueContext,
                                 @Nonnull ValidatorContext validatorContext) {
    var column = valueContext.getColumn();
    int row = valueContext.getRow();
    var schemaColumn = validatorContext.getValidationSettings().getSchemaColumn();
    if (column.equals(schemaColumn)) {
      var templateId = String.valueOf(value);
      var exists = cedarService.checkCedarTemplateExists(templateId);
      if (!exists) {
        validatorContext.getValidationResult().add(
          ValidationError.builder()
            .setColumnName(column)
            .setRowNumber(row)
            .setErrorDescription("Schema does not exist")
            .setProp(VALUE, value)
            .setProp(ERROR_TYPE, "invalidSchemaId")
            .setProp(SEVERITY, 1)
            .build()
        );
      }
    }
  }
}
