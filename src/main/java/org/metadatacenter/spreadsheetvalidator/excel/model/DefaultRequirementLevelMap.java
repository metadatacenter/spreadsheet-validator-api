package org.metadatacenter.spreadsheetvalidator.excel.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DefaultRequirementLevelMap implements RequirementLevelMap {

  private final static Map<String, Boolean> REQUIREMENT_LEVEL_MAP = ImmutableMap.of(
      "REQUIRED", true,
      "OPTIONAL", false,
      "RECOMMENDED", false
  );

  @Override
  public boolean isRequired(String requirementLevelString) {
    return REQUIREMENT_LEVEL_MAP.getOrDefault(requirementLevelString, false);
  }

  @Override
  public List<String> getSupportedLevels() {
    return ImmutableList.copyOf(REQUIREMENT_LEVEL_MAP.keySet());
  }
}
