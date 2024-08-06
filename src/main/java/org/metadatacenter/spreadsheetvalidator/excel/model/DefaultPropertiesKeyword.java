package org.metadatacenter.spreadsheetvalidator.excel.model;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DefaultPropertiesKeyword implements PropertiesKeyword {

  @Override
  public String ofMetaSchemaId() {
    return "Header schema ID";
  }

  @Override
  public String ofAuthors() {
    return "Authors";
  }

  @Override
  public String ofTitle() {
    return "Title";
  }

  @Override
  public String ofVersion() {
    return "Version";
  }
}
