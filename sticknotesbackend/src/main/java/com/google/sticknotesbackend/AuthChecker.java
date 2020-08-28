package com.google.sticknotesbackend;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sticknotesbackend.enums.Permission;
import com.google.sticknotesbackend.enums.Role;
import com.google.sticknotesbackend.models.Note;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.UserBoardRole;
import com.google.sticknotesbackend.models.Whiteboard;
import java.util.List;

/**
 * Class provides static methods that check if user has permission to access different resources of the app
 */
public class AuthChecker {
  /**
   * Checks if current user can modify(edit/delete) the given board
   */
  public static Permission boardModifyPermission(Whiteboard board) {
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
      List<UserBoardRole> roles =
          ofy().load().type(UserBoardRole.class).filter("board", board).filter("user", user).list();
      for (UserBoardRole role : roles) {
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
  public static Permission boardAccessPermission(Long boardId) {
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      // get current user
      User user =
          ofy().load().type(User.class).filter("googleAccId", userService.getCurrentUser().getUserId()).first().now();
      // find the first role with the user and board
      UserBoardRole role =
          ofy().load().type(UserBoardRole.class).filter("boardId", boardId).filter("user", user).first().now();
      if (role != null) {
        return Permission.GRANTED;
      }
      return Permission.FORBIDDEN;
    }
    return Permission.NOAUTH;
  }

  /**
   * Checks if current user has enough permissions to modify the note (edit/delete)
   */
  public static Permission noteModifyPermission(Note note) {
    // check that user is authenticated
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      // if the user is author of the note, allow modify
      if (note.getCreator().googleAccId.equals(userService.getCurrentUser().getUserId())) {
        return Permission.GRANTED;
      }
      // if there is a role ADMIN or OWNER with this board and this user, then delete the note
      // get current user
      User user =
          ofy().load().type(User.class).filter("googleAccId", userService.getCurrentUser().getUserId()).first().now();
      // get the list of rules for this board and user
      List<UserBoardRole> boardRoles =
          ofy().load().type(UserBoardRole.class).filter("boardId", note.boardId).filter("user", user).list();
      for (UserBoardRole role : boardRoles) {
        if (role.role.equals(Role.ADMIN)) {
          return Permission.GRANTED;
        }
      }
      return Permission.FORBIDDEN;
    }
    return Permission.NOAUTH;
  }
}
