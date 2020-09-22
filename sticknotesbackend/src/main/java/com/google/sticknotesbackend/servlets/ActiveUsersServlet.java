/**
 * Copyright 2020 Google LLC
 * 
 * Servlet for handling requests for active users list
 */
package com.google.sticknotesbackend.servlets;

import java.io.IOException;
import java.util.List;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.JsonObject;

import com.google.sticknotesbackend.ActiveUsersManager;
import com.google.sticknotesbackend.AuthChecker;
import com.google.sticknotesbackend.FastStorage;
import com.google.sticknotesbackend.enums.Permission;
import com.google.sticknotesbackend.models.UserBoardRole;

@WebServlet("api/active-users/")
public class ActiveUsersServlet extends AppAbstractServlet {
  //if user was not active for more that 5sec it will be determined as inactive
  public final int activityTimeMillis = 5000;

  /**
   * To fetch active users list user needs the same permissions as for accessing
   * the board. After permission is checked, last activity of users role on the board
   * is updated and active users are retrieved from memcache. 
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String boardIdParam = request.getParameter("id");
    // optional language param
    if (boardIdParam != null) {
      long boardId = Long.parseLong(boardIdParam);
      // check if user can access the board
      Permission perm = AuthChecker.boardAccessPermission(boardId);
      if (!perm.equals(Permission.GRANTED)) {
        handleBadPermission(perm, response);
        return;
      }

      UserService userService = UserServiceFactory.getUserService();
      // get user's google acc id
      String googleAccId = userService.getCurrentUser().getUserId();
      // get users role
      UserBoardRole role = FastStorage.getUserBoardRole(boardId, googleAccId);

      // updating role that sent the request
      ActiveUsersManager.updateUserActivity(boardId, role.id, System.currentTimeMillis());

      // getting active roles
      List<Long> activeIds = ActiveUsersManager.getActiveIds(boardId, System.currentTimeMillis(), activityTimeMillis);

      // preparing response body
      Gson gson = new Gson();
      JsonObject responseBody = new JsonObject();
      responseBody.add("activeUsers", gson.toJsonTree(activeIds));
      response.getWriter().print(responseBody.toString());
    } else {
      badRequest("No id parameter", response);
    }
  }
}
