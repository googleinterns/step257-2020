package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.sticknotesbackend.enums.Permission;
import com.google.sticknotesbackend.enums.Role;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.UserBoardRole;
import com.google.sticknotesbackend.models.Whiteboard;
import com.google.sticknotesbackend.serializers.WhiteboardSerializer;
import java.util.List;

/**
 * Provides a common logic for both EditBoardServlet and BoardServlet
 */
public abstract class BoardAbstractServlet extends AppAbstractServlet {
  /**
   * Generates a Gson object that uses custom WhiteboardSerializer when
   * serializing Whiteboard objects.
   */
  public Gson getBoardGsonParser() {
    GsonBuilder gson = new GsonBuilder();
    gson.registerTypeAdapter(Whiteboard.class, new WhiteboardSerializer());
    Gson parser = gson.create();
    return parser;
  }
  /**
   * Checks if current user can modify(edit/delete) the given board
   * @return
   */
  public Permission boardModifyPermission(Whiteboard board) {
    // check that user is authenticated
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      User user =
          ofy().load().type(User.class).filter("googleAccId", userService.getCurrentUser().getUserId()).first().now();
      // if user is owner of the board then access is granted
      if (board.getCreator().googleAccId.equals(user.googleAccId)) {
        return Permission.GRANTED;
      }
      // otherwise find user's role
      List<UserBoardRole> roles = ofy().load().type(UserBoardRole.class).filter("board", board).filter("user", user).list();
      for (UserBoardRole role: roles) {
        if (role.role.equals(Role.ADMIN)) {
          return Permission.GRANTED;
        }
      }
      return Permission.FORBIDDEN;
    }
    return Permission.NOAUTH;
  }

  /**
   * Checks if user can access the board with the given id
   */
  public Permission boardAccessPermission(Long boardId) {
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      // get current user
      User user =
          ofy().load().type(User.class).filter("googleAccId", userService.getCurrentUser().getUserId()).first().now();
      // find the first role with the user and board 
      UserBoardRole role = ofy().load().type(UserBoardRole.class).filter("boardId", boardId).filter("user", user).first().now();
      if (role != null) {
        return Permission.GRANTED;
      }
      return Permission.FORBIDDEN;
    }
    return Permission.NOAUTH;
  }
}
