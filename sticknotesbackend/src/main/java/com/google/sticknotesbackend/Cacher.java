package com.google.sticknotesbackend;

import com.google.sticknotesbackend.enums.Permission;
import com.google.sticknotesbackend.models.UserBoardRole;
import com.google.sticknotesbackend.models.Whiteboard;
import java.util.Collections;
import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

public class Cacher {
  private static Cache cacheInstance;
  private static Cache getCacheInstance() {
    if (cacheInstance == null) {
      try {
        CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
        cacheInstance = cacheFactory.createCache(Collections.emptyMap());
      } catch (CacheException e) {
        return null;
      }
    }
    return cacheInstance;
  }

  /**
   * Saves whiteboard object in cache
   */
  public static void saveBoard(Whiteboard board) {
    // a key is board id
    String key = Long.toString(board.id);
    Cache cache = getCacheInstance();
    cache.put(key, board);
  }

  /**
   * Retrieves whiteboard object from cache if it is there.
   * Otherwise returns null
   */
  public static Whiteboard getBoard(String boardId) {
    // a key is board id
    Cache cache = getCacheInstance();
    return (Whiteboard)cache.get(boardId);
  }

  /**
   * Stores a permission in cache
   */
  public static void savePermission(String boardId, String googleAccId, Permission perm) {
    // generate a key of type "boardid-user's googleAccId"
    String key = boardId + "-" + googleAccId;
    Cache cache = getCacheInstance();
    cache.put(key, perm);
  }

  /**
   * Retrieves a permission from cache by board id and user's google acc id
   */
  public static Permission getPermission(String boardId, String googleAccId) {
    // generate a key of type "boardid-user's googleAccId"
    String key = boardId + "-" + googleAccId;
    Cache cache = getCacheInstance();
    return (Permission)cache.get(key);
  }
}
