package org.metadatacenter.spreadsheetvalidator.excel.model;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DefaultSchemaKeyword implements SchemaKeyword {

  @Override
  public String ofVariable() {
    return "Variable";
  }

  @Override
  public String ofLabel() {
    return "Label";
  }

  @Override
  public String ofDatatype() {
    return "Type";
  }

  @Override
  public String ofRequirementLevel() {
    return "Priority";
  }

  @Override
  public String ofDescription() {
    return "Description";
  }

  @Override
  public String ofInputExample() {
    return "Example";
  }

  @Override
  public String ofMinValue() {
    return "Min value";
  }

  @Override
  public String ofMaxValue() {
    return "Max value";
  }

  @Override
  public String ofInputPattern() {
    return "Input pattern";
  }

  @Override
  public String ofPermissibleValues() {
    return "Permissible values";
  }
}
