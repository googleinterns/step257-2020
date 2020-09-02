package com.google.sticknotesbackend;

import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

public class Cacher {
  private static Cache cache;
  /**
   * Used as a return parameter from cache "get" operation
   * Stores last update, so user of this class can check if they need this cached data or not
   */
  public static class BoardCacheEntry {
    public String lastUpdatedTimestamp;
    public String board;
  };

  private static Cache getInstance() {
    if (cache == null) {
      try {
        CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
        Map<Object, Object> properties = new HashMap<>();
        // set cache expiration to one hour
        properties.put(GCacheFactory.EXPIRATION_DELTA, TimeUnit.HOURS.toSeconds(1));
        cache = cacheFactory.createCache(properties);
        return cache;
      } catch (CacheException e) {
        return null;
      }
    } else {
      return cache;
    }
  }
  /**
   * Stores board json in Memcache. Stores the timestamp when the board was last updated
   */
  public static void storeBoardInCache(String boardId, String boardJson, String languageCode) {
    // get cache instance
    Cache cache = getInstance();
    if (cache != null) {
      // generate board key
      String boardKey = boardId + "-" + (languageCode == null ? "" : languageCode);
      // store the board in cache
      cache.put(boardKey, boardJson);
      // generate board last updated timestamp key
      String boardLastUpdateTimestampKey = boardKey + "-time";
      // generate and save timestamp
      cache.put(boardLastUpdateTimestampKey, Long.toString(System.currentTimeMillis()));
    }
  }

  /**
   * Returns the board json string currently stored in cache with the notes in specified language as well as the
   * timestamp of when the record was added to the cache
   */
  public static BoardCacheEntry getBoardFromCache(String boardId, String languageCode) {
    // get cache instance
    Cache cache = getInstance();
    if (cache != null) {
      // get the key of board entry
      String boardKey = boardId + "-" + (languageCode == null ? "" : languageCode);
      // check if such key exists in cache
      if (cache.containsKey(boardKey)) {
        // fetch the board from cache
        BoardCacheEntry entry = new BoardCacheEntry();
        entry.board = (String)cache.get(boardKey);
        // fetch the timestamp and the board itself
        String boardLastUpdateTimestampKey = boardKey + "-time";
        entry.lastUpdatedTimestamp = (String)cache.get(boardLastUpdateTimestampKey);
        return entry;
      }
    }
    return null;
  }
}
