package org.metadatacenter.spreadsheetvalidator.algorithm;

import com.google.common.collect.ImmutableList;
import org.apache.commons.compress.utils.Lists;
import org.metadatacenter.spreadsheetvalidator.domain.ColumnDescription;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetRow;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetDefinition;
import org.metadatacenter.spreadsheetvalidator.util.ValueAssertion;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CompletenessChecker {

  private final SpreadsheetDefinition spreadsheetDefinition;

  @Inject
  public CompletenessChecker(@Nonnull SpreadsheetDefinition spreadsheetDefinition) {
    this.spreadsheetDefinition = checkNotNull(spreadsheetDefinition);
  }

  public ImmutableList<CompletenessCheckerResult> check(SpreadsheetRow spreadsheetRow) {
    var resultList = Lists.<CompletenessCheckerResult>newArrayList();
    spreadsheetDefinition.getColumnDescriptionStream()
        .filter(ColumnDescription::isRequiredColumn)
        .forEach(columnDescription -> {
          var rowNumber = spreadsheetRow.getRowNumber();
          var columnName = columnDescription.getColumnName();
          var value = spreadsheetRow.getValue(columnName);
          if (ValueAssertion.of(value).isNullOrEmpty()) {
            resultList.add(CompletenessCheckerResult.create(rowNumber, columnName, ErrorType.MISSING_REQUIRED));
          }
        });
    return ImmutableList.copyOf(resultList);
  }
}
