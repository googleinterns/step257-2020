/** 
 * ActiveUsersManager is used to access an mange memcache connected with active users lists.
*/

package com.google.sticknotesbackend;

import java.util.ArrayList;
import java.util.List;
import com.google.sticknotesbackend.models.ActiveUsers;
import org.javatuples.Pair;
import javax.cache.Cache;
import java.util.stream.Collectors;

public class ActiveUsersManager extends LocalCacheManager {
  private static final String cacheKeyPrefix = "activeUsers#";
  /**
   * 
   * @param userId
   * @param timestamp
   * 
   * if user already exists in the list, updates its last activity timestamp,
   * if user doesn't exist in the list, user is added to the list
   */
  public static void updateUserActivity(Long boardId, Long userId, Long timestamp) {
    Cache cache = getCacheInstance();
    String cacheKey = cacheKeyPrefix + Long.toString(boardId);
    ActiveUsers activeUsers = (ActiveUsers) cache.get(cacheKey);
    if (activeUsers == null) {
      activeUsers = new ActiveUsers(boardId);
    }
    boolean userFound = false;
    for(int i = 0; i < activeUsers.usersLastActivity.size(); i++){
      if(activeUsers.usersLastActivity.get(i).getValue0().equals(userId)){
        userFound = true;
        activeUsers.usersLastActivity.set(i, activeUsers.usersLastActivity.get(i).setAt1(timestamp));
        break;
      }
    }
    if(!userFound) {
      activeUsers.usersLastActivity.add(new Pair<Long, Long>(userId, timestamp));
    }
    cache.put(cacheKey, activeUsers);
  }

  /**
   * 
   * @param boardId - board for which active users should be retrieved
   * @param now - timestamp that represents current moment
   * @param expTimeMillis - time in millis after which user should be determined as inactive
   * @return - list of active users
   * 
   * Function retrieves list of users from cache and filters out users with lastActivity younger 
   * than now-expirationTime
   */
  public static List<Long> getActiveIds(Long boardId, Long now, int expTimeMillis) {
    Cache cache = getCacheInstance();
    String cacheKey = cacheKeyPrefix + Long.toString(boardId);
    ActiveUsers activeUsers = (ActiveUsers) cache.get(cacheKey);
    if (activeUsers == null) {
      return new ArrayList<>();
    }
    return activeUsers.usersLastActivity.stream().filter(pair -> now - pair.getValue1() < expTimeMillis)
        .map(pair -> pair.getValue0()).collect(Collectors.toCollection(ArrayList::new));
  }
}
