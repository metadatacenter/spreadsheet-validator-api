package org.metadatacenter.spreadsheetvalidator.util;

import com.google.common.collect.ImmutableList;
import org.apache.commons.compress.utils.Lists;
import org.metadatacenter.spreadsheetvalidator.algorithm.AdherenceCheckerResult;
import org.metadatacenter.spreadsheetvalidator.algorithm.CompletenessCheckerResult;
import org.metadatacenter.spreadsheetvalidator.response.ValidateResponse;

import javax.validation.Valid;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ResponseBuilder {

  private final List<ValidateResponse> responseList = Lists.newArrayList();

  public ResponseBuilder append(CompletenessCheckerResult checkerResult) {
    responseList.add(ValidateResponse.create(
        checkerResult.getRowNumber(),
        checkerResult.getColumnName(),
        null,
        null,
        checkerResult.getErrorType().getCanonicalName()));
    return this;
  }

  public ResponseBuilder append(AdherenceCheckerResult checkerResult) {
    responseList.add(ValidateResponse.create(
        checkerResult.getRowNumber(),
        checkerResult.getColumnName(),
        checkerResult.getActualValue(),
        checkerResult.getSuggestedValue(),
        checkerResult.getErrorType().getCanonicalName()));
    return this;
  }

  public Response build() {
    return Response.ok(ImmutableList.copyOf(responseList)).build();
  }
}
