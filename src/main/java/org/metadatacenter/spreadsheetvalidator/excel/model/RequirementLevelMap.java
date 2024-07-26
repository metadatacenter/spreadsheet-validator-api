package org.metadatacenter.spreadsheetvalidator.excel.model;

import java.util.List;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface RequirementLevelMap {

  boolean isRequired(String requirementLevelString);

  List<String> getSupportedLevels();
}
