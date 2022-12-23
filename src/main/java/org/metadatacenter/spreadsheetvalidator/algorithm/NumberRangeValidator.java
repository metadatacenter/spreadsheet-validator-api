package org.metadatacenter.spreadsheetvalidator.algorithm;

import org.metadatacenter.spreadsheetvalidator.SpreadsheetValidator;
import org.metadatacenter.spreadsheetvalidator.Validator;
import org.metadatacenter.spreadsheetvalidator.ValidatorContext;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetRow;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NumberRangeValidator implements Validator {
  @Override
  public void validate(@Nonnull ValidatorContext context, @Nonnull SpreadsheetRow spreadsheetRow) {
    // No implementation
  }

  @Override
  public void chain(@Nonnull SpreadsheetValidator spreadsheetValidator, @Nonnull List<SpreadsheetRow> spreadsheetRows) {
    spreadsheetValidator
        .onEach(spreadsheetRows, new MinRangeValidator())
        .onEach(spreadsheetRows, new MaxRangeValidator());
  }
}
