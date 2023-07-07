package org.metadatacenter.spreadsheetvalidator.thirdparty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class OntologyValue {

  private static final String AT_ID = "@id";
  private static final String AT_TYPE = "@type";
  private static final String ID = "id";
  private static final String TYPE = "type";
  private static final String PREF_LABEL = "prefLabel";
  private static final String NOTATION = "notation";
  private static final String DEFINITION = "definition";
  private static final String SOURCE = "source";
  private static final String MATCH_TYPE = "matchType";
  private static final String MATCHED_SYNONYMS = "matchedSynonyms";

  @Nonnull
  @JsonCreator
  public static OntologyValue create(@Nonnull @JsonProperty(AT_ID) String iri,
                                     @Nonnull @JsonProperty(AT_TYPE) String typeIri,
                                     @Nonnull @JsonProperty(ID) String id,
                                     @Nonnull @JsonProperty(TYPE) String type,
                                     @Nonnull @JsonProperty(PREF_LABEL) String prefLabel,
                                     @JsonProperty(NOTATION) String notation,
                                     @JsonProperty(DEFINITION) String definition,
                                     @Nonnull @JsonProperty(SOURCE) String source,
                                     @JsonProperty(MATCH_TYPE) String matchType,
                                     @Nonnull @JsonProperty(MATCHED_SYNONYMS) ImmutableList<String> matchedSynonyms) {
    return new AutoValue_OntologyValue(iri, typeIri, id, type, prefLabel, notation, definition, source, matchType, matchedSynonyms);
  }

  @Nonnull
  @JsonProperty(AT_ID)
  public abstract String getIri();

  @Nonnull
  @JsonProperty(AT_TYPE)
  public abstract String getTypeIri();

  @Nonnull
  @JsonProperty(ID)
  public abstract String getId();

  @Nonnull
  @JsonProperty(TYPE)
  public abstract String getType();

  @Nonnull
  @JsonProperty(PREF_LABEL)
  public abstract String getPrefLabel();

  @Nullable
  @JsonProperty(NOTATION)
  public abstract String getNotation();

  @Nullable
  @JsonProperty(DEFINITION)
  public abstract String getDefinition();

  @Nonnull
  @JsonProperty(SOURCE)
  public abstract String getSource();

  @Nullable
  @JsonProperty(MATCH_TYPE)
  public abstract String getMatchType();

  @Nonnull
  @JsonProperty(MATCHED_SYNONYMS)
  public abstract ImmutableList<String> getMatchedSynonyms();
}
