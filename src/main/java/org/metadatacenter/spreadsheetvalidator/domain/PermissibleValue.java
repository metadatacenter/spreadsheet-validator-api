package org.metadatacenter.spreadsheetvalidator.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class PermissibleValue {

  private static final String LABEL = "label";
  private static final String MEANING = "meaning";

  @Nonnull
  @JsonCreator
  public static PermissibleValue create(@Nonnull @JsonProperty(LABEL) String label,
                                        @Nullable @JsonProperty(MEANING) String meaning) {
    return new AutoValue_PermissibleValue(label, meaning);
  }

  @Nonnull
  @JsonProperty(LABEL)
  public abstract String getLabel();

  @Nullable
  @JsonProperty(MEANING)
  public abstract String getMeaning();
}
