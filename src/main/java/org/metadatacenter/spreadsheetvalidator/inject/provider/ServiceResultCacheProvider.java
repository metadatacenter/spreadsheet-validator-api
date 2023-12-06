package org.metadatacenter.spreadsheetvalidator.inject.provider;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Singleton
public class ServiceResultCacheProvider implements Provider<Cache<String, String>> {

  @Override
  public Cache<String, String> get() {
    return Caffeine.newBuilder()
        .expireAfterWrite(30, TimeUnit.DAYS)
        .maximumSize(100)
        .build();
  }
}

