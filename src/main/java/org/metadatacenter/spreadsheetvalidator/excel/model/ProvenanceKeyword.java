package org.metadatacenter.spreadsheetvalidator.excel.model;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface ProvenanceKeyword {

  String ofSchemaName();

  String ofSchemaVersion();

  String ofCreationDate();

  String ofAccessUrl();
}
