package org.metadatacenter.spreadsheetvalidator.excel.model;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface ReservedKeyword {

  String ofVariable();

  String ofDatatype();

  String ofRequirementLevel();

  String ofDescription();

  String ofInputExample();

  String ofMinValue();

  String ofMaxValue();

  String ofInputPattern();

  String ofPermissibleValues();
}
