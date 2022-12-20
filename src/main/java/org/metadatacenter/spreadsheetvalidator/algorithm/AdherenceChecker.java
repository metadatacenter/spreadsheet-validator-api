package org.metadatacenter.spreadsheetvalidator.algorithm;

import com.google.common.collect.ImmutableList;
import org.apache.commons.compress.utils.Lists;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetDefinition;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetRow;
import org.metadatacenter.spreadsheetvalidator.util.ValueAssertion;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AdherenceChecker {

  private final SpreadsheetDefinition spreadsheetDefinition;

  private final AutoSuggester autoSuggester;

  @Inject
  public AdherenceChecker(@Nonnull SpreadsheetDefinition spreadsheetDefinition,
                          @Nonnull AutoSuggester autoSuggester) {
    this.spreadsheetDefinition = checkNotNull(spreadsheetDefinition);
    this.autoSuggester = checkNotNull(autoSuggester);
  }

  public ImmutableList<AdherenceCheckerResult> check(SpreadsheetRow spreadsheetRow) {
    var resultList = Lists.<AdherenceCheckerResult>newArrayList();
    spreadsheetDefinition.getColumnDescriptionStream()
        .forEach(columnDescription -> {
          var rowNumber = spreadsheetRow.getRowNumber();
          var columnName = columnDescription.getColumnName();
          var actualValue = spreadsheetRow.getValue(columnName);
          if (!ValueAssertion.of(actualValue).isNullOrEmpty()) {
            var valueType = columnDescription.getColumnType();
            switch (valueType) {
              case STRING:
                if (!ValueAssertion.of(actualValue).isString()) {
                  var suggestedValue = autoSuggester.replace(actualValue).asString();
                  var checkerResult = AdherenceCheckerResult.create(
                      rowNumber,
                      columnName,
                      actualValue,
                      suggestedValue,
                      ErrorType.NOT_STRING_TYPE
                  );
                }
            }
          }
        });
    return ImmutableList.copyOf(resultList);
  }
}
