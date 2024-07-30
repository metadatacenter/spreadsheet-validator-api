package org.metadatacenter.spreadsheetvalidator.excel.model;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DefaultProvenanceKeyword implements ProvenanceKeyword {

  @Override
  public String ofSchemaName() {
    return "schema:title";
  }

  @Override
  public String ofSchemaVersion() {
    return "pav:version";
  }

  @Override
  public String ofCreationDate() {
    return "pav:createdOn";
  }

  @Override
  public String ofAccessUrl() {
    return "pav:derivedFrom";
  }
}
