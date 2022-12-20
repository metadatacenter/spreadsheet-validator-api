package org.metadatacenter.spreadsheetvalidator.inject.module;

import dagger.Module;
import dagger.Provides;
import org.metadatacenter.spreadsheetvalidator.algorithm.CompletenessChecker;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetDefinition;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class CompletenessCheckerModule {

  private final SpreadsheetDefinition spreadsheetDefinition;

  public CompletenessCheckerModule(@Nonnull SpreadsheetDefinition spreadsheetDefinition) {
    this.spreadsheetDefinition = checkNotNull(spreadsheetDefinition);
  }

  @Provides
  public CompletenessChecker provideCompletenessChecker() {
    return new CompletenessChecker(spreadsheetDefinition);
  }
}
