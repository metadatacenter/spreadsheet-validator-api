package org.metadatacenter.spreadsheetvalidator.inject.module;

import dagger.Module;
import dagger.Provides;
import org.metadatacenter.spreadsheetvalidator.algorithm.AdherenceChecker;
import org.metadatacenter.spreadsheetvalidator.domain.SpreadsheetDefinition;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class AdherenceCheckerModule {

  private final SpreadsheetDefinition spreadsheetDefinition;

  public AdherenceCheckerModule(@Nonnull SpreadsheetDefinition spreadsheetDefinition) {
    this.spreadsheetDefinition = checkNotNull(spreadsheetDefinition);
  }
}
