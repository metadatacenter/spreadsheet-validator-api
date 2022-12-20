package org.metadatacenter.spreadsheetvalidator;

import org.metadatacenter.spreadsheetvalidator.algorithm.AdherenceChecker;
import org.metadatacenter.spreadsheetvalidator.algorithm.CompletenessChecker;
import org.metadatacenter.spreadsheetvalidator.domain.Spreadsheet;
import org.metadatacenter.spreadsheetvalidator.util.ResponseBuilder;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class SpreadsheetValidator {

  private final CompletenessChecker completenessChecker;

  private final AdherenceChecker adherenceChecker;

  @Inject
  public SpreadsheetValidator(@NotNull CompletenessChecker completenessChecker,
                              @NotNull AdherenceChecker adherenceChecker) {
    this.completenessChecker = checkNotNull(completenessChecker);
    this.adherenceChecker = checkNotNull(adherenceChecker);
  }

  public Response validate(Spreadsheet spreadsheet) {
    var responseBuilder = new ResponseBuilder();
    spreadsheet.getRowStream()
        .forEach((spreadsheetRow) -> {
          var checkResults = completenessChecker.check(spreadsheetRow);
          checkResults.forEach(responseBuilder::append);
        });
    return responseBuilder.build();
  }
}
