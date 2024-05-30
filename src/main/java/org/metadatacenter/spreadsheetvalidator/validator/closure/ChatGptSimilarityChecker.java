package org.metadatacenter.spreadsheetvalidator.validator.closure;

import org.metadatacenter.spreadsheetvalidator.thirdparty.ChatGptService;
import org.metadatacenter.spreadsheetvalidator.thirdparty.ServiceNotAvailable;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ChatGptSimilarityChecker implements Closure<String> {

  private final ChatGptService chatGptService;

  private final SimpleSimilarityChecker simpleChecker;

  private final boolean ENABLE_CHATGPT = true;

  @Inject
  public ChatGptSimilarityChecker(@Nonnull ChatGptService chatGptService,
                                  @Nonnull SimpleSimilarityChecker simpleChecker) {
    this.chatGptService = checkNotNull(chatGptService);
    this.simpleChecker = checkNotNull(simpleChecker);
  }

  @Override
  public String execute(Object... inputs) {
    var fieldName = String.valueOf(inputs[0]);
    var userInput = String.valueOf(inputs[1]);
    var permissibleValues = (List<String>) inputs[2];
    var prompt = getPrompt(fieldName, userInput, permissibleValues);
    var suggestion = "";
    if (ENABLE_CHATGPT) {
      try {
        suggestion = chatGptService.getResponse(prompt);
      } catch (ServiceNotAvailable e) {
        suggestion = simpleChecker.execute(userInput, permissibleValues);
      }
    } else {
      suggestion = simpleChecker.execute(userInput, permissibleValues);
    }
    return suggestion;
  }

  private String getPrompt(String fieldName, String userInput, List<String> permissibleValues) {
    var sb = new StringBuilder();
    sb.append("Input: Field Name - Unit of Time, Permitted values - [hour, second, minute], User Input - h; Output: hour");
    sb.append("\n");
    sb.append("Input: Field Name - ");
    sb.append(fieldName);
    sb.append(", Permitted values - ");
    sb.append(permissibleValues);
    sb.append(", User Input - ");
    sb.append(userInput);
    sb.append("; Output: ");
    return sb.toString();
  }
}
