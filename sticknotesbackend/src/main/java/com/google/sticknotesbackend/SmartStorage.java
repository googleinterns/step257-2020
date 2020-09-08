package com.google.sticknotesbackend;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.appengine.api.memcache.stdimpl.GCacheFactory;
import com.google.sticknotesbackend.enums.Permission;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.UserBoardRole;
import com.google.sticknotesbackend.models.Whiteboard;
import com.googlecode.objectify.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

/**
 * This class handles all operations with objectify and cache, ensures data consistency
 */
public class SmartStorage {
  private static Cache cacheInstance;
  private static Cache getCacheInstance() {
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

  /**
   * Updates the board in datastore.
   * Updates the "lastUpdated" field.
   * Stores the board in cache
   */
  public static void updateBoard(Whiteboard board) {
    board.lastUpdated = System.currentTimeMillis();
    ofy().save().entity(board).now();
    // insert board in cache
    Cache cache = getCacheInstance();
    // store board in cache as well, use board id as cache key
    cache.put(Long.toString(board.id), board);
  }

  /**
   * Returns the board with given id if it exists or null otherwise
   * Tries to get board from cache, if cache is empty, loads board from datastore
   * Loads all fields except notes and creator
   */
  public static Whiteboard getWhiteboardLite(Long boardId) {
    // try to load board from cache
    Cache cache = getCacheInstance();
    String cacheKey = Long.toString(boardId);
    Whiteboard board = (Whiteboard)cache.get(cacheKey);
    if (board == null) {
      // if cache is empty, load from datastore
      Key<Whiteboard> boardKey = Key.create(Whiteboard.class, boardId);
      board = ofy().load().group(Whiteboard.WithoutNotesAndCreator.class).key(boardKey).now();
      // save board in cache for future use
      cache.put(cacheKey, board);
      return board;
    }
    return board;
  }

  /**
   * Returns a UserBoardRole object for given board and user with given google acc id.
   * Tries to get value from cache, if it is empty gets value from datastore and updates cache
   */
  public static UserBoardRole getUserBoardRole(Long boardId, String googleAccId) {
    Cache cache = getCacheInstance();
    String cacheKey = Long.toString(boardId) + "-" + googleAccId;
    UserBoardRole role = (UserBoardRole)cache.get(cacheKey);
    if (role == null) {
      // permission is not in cache yet, so load it and put into cache
      // get current user
      User user = ofy().load().type(User.class).filter("googleAccId", googleAccId).first().now();
      // find the first role with the user and board
      role = ofy().load().type(UserBoardRole.class).ancestor(Key.create(Whiteboard.class, boardId)).filter("user", user).first().now();
      // store role in cache
      // of course if role doesn't exist it will store null in cache, and later calls will not utilize cache
      // for now to keep things simpler let's not think about it
      cache.put(cacheKey, role);
    }
    return role;
  }
}
