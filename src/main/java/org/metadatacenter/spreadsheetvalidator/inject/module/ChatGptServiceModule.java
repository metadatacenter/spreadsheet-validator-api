package org.metadatacenter.spreadsheetvalidator.inject.module;

import dagger.Module;
import dagger.Provides;
import org.metadatacenter.spreadsheetvalidator.inject.provider.ServiceResultCacheProvider;
import org.metadatacenter.spreadsheetvalidator.thirdparty.ChatGptConfig;
import org.metadatacenter.spreadsheetvalidator.thirdparty.ChatGptService;
import org.metadatacenter.spreadsheetvalidator.thirdparty.RestServiceHandler;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = {
    WebResourceModule.class,
    RestServiceModule.class
})
public class ChatGptServiceModule {

  @Provides
  public ChatGptService getChatGptService(ChatGptConfig config,
                                          RestServiceHandler restServiceHandler) {
    return new ChatGptService(config, restServiceHandler, new ServiceResultCacheProvider().get());
  }
}
