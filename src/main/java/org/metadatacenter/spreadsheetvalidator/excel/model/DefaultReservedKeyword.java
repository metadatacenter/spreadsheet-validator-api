package org.metadatacenter.spreadsheetvalidator.excel.model;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DefaultReservedKeyword implements ReservedKeyword {

  @Override
  public String ofVariable() {
    return "variable";
  }

  @Override
  public String ofDatatype() {
    return "type";
  }

  @Override
  public String ofRequirementLevel() {
    return "priority";
  }

  @Override
  public String ofDescription() {
    return "description";
  }

  @Override
  public String ofInputExample() {
    return "example";
  }

  @Override
  public String ofMinValue() {
    return "min_value";
  }

  @Override
  public String ofMaxValue() {
    return "max_value";
  }

  @Override
  public String ofInputPattern() {
    return "input_pattern";
  }

  @Override
  public String ofPermissibleValues() {
    return "permissible_values";
  }
}
