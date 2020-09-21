/**
 * Class in a super class for memcache management classes
 */
package com.google.sticknotesbackend;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

import com.google.appengine.api.memcache.stdimpl.GCacheFactory;

public class LocalCacheManager {
  protected static Cache cacheInstance;
  protected static Cache getCacheInstance() {
    if (cacheInstance == null) {
      try {
        CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
        // set cache expiration to one hour
        Map<Object, Object> properties = new HashMap<>();
        properties.put(GCacheFactory.EXPIRATION_DELTA, TimeUnit.HOURS.toSeconds(1));
        cacheInstance = cacheFactory.createCache(properties);
      } catch (CacheException e) {
        return null;
      }
    }
    return cacheInstance;
  }
}
