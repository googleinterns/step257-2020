package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.sticknotesbackend.AuthChecker;
import com.google.sticknotesbackend.FastStorage;
import com.google.sticknotesbackend.JsonParsers;
import com.google.sticknotesbackend.enums.Permission;
import com.google.sticknotesbackend.enums.Role;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.UserBoardRole;
import com.google.sticknotesbackend.models.Whiteboard;
import com.googlecode.objectify.Key;
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("api/board/users/")
public class UserListServlet extends AppAbstractServlet {
  // with a given id it returns list of users of the board
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // authorization check
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      unauthorized(response);
      return;
    }
    String boardIdParam = request.getParameter("id");
    if (boardIdParam != null) {
      Long boardId = Long.valueOf(boardIdParam);
      Whiteboard board = ofy().load().type(Whiteboard.class).id(boardId).now();
      if (board != null) {
        List<UserBoardRole> boardUsers = ofy().load().type(UserBoardRole.class).ancestor(board).list();
        Gson userBoardRoleParser = JsonParsers.getBoardRoleGsonParser();

        String responseJson = userBoardRoleParser.toJson(boardUsers);

        response.setStatus(OK);
        response.setContentType("application/json");
        response.getWriter().println(responseJson);
        return;
      }
      badRequest("Board with this id doesn't exist", response);
      return;
    }
    badRequest("Error while reading request param.", response);
    return;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // authorization check
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      unauthorized(response);
      return;
    }

    String boardIdParam = request.getParameter("id");
    if (boardIdParam == null) {
      badRequest("Error while reading request param.", response);
      return;
    }

    Long boardId = Long.parseLong(boardIdParam);

    Gson gson = JsonParsers.getBoardRoleGsonParser();
    JsonObject body = JsonParser.parseReader(request.getReader()).getAsJsonObject();
    if (!body.has("email") || !body.has("role")) {
      badRequest("Request has to contain 'email' and 'role' property", response);
      return;
    }
    String email = body.get("email").getAsString();
    Role role = null;
    try {
      role = Role.valueOf(body.get("role").getAsString().toUpperCase());
    } catch (IllegalArgumentException e) {
      badRequest("Role has to be one of: admin, user, owner", response);
      return;
    }

    Whiteboard board = ofy().load().type(Whiteboard.class).id(boardId).now();
    if (board == null) {
      badRequest("Board with a given id not found.", response);
      return;
    }

    Permission perm = AuthChecker.userListModifyPermission(role, board);
    if (perm != Permission.GRANTED) {
      handleBadPermission(perm, response);
      return;
    }

    User user = ofy().load().type(User.class).filter("email", email).first().now();
    if (user == null) {
      user = new User(email, "---");
      ofy().save().entity(user).now();
    }

    UserBoardRole roleFromDatastore = ofy().load().type(UserBoardRole.class).ancestor(board).filter("user", user)
        .first().now();

    if (roleFromDatastore != null) {
      badRequest("User already in the list.", response);
      return;
    }
    UserBoardRole userBoardRole = new UserBoardRole(role, board, user);

    ofy().save().entity(userBoardRole).now();

    response.getWriter().println(gson.toJson(userBoardRole));
    response.setStatus(OK);
    return;
  }

  /**
   * doDelete needs two params passed in the request: board-id and id (role id),
   * two params are needed because there is parent relationship between role and a
   * board, because of that fact the role can't be identified only by it's id
   * anymore, it's key consists of parent key and it's id
   */
  @Override
  public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // authorization check
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      unauthorized(response);
      return;
    }

    String boardRoleIdParam = request.getParameter("id");
    String boardIdParam = request.getParameter("board-id");
    if (boardRoleIdParam == null) {
      badRequest("Error while reading request param.", response);
      return;
    }

    Long boardRoleId = Long.valueOf(boardRoleIdParam);
    Long boardId = Long.valueOf(boardIdParam);
    Key<Whiteboard> boardKey = Key.create(Whiteboard.class, boardId);
    UserBoardRole boardRole = ofy().load().type(UserBoardRole.class).ancestor(boardKey)
        .filterKey(Key.create(boardKey, UserBoardRole.class, boardRoleId)).first().now();
    if (boardRole == null) {
      badRequest("Role with a given id not found.", response);
      return;
    }

    Permission perm = AuthChecker.userListModifyPermission(boardRole.role, boardRole.getBoard());
    if (perm != Permission.GRANTED) {
      handleBadPermission(perm, response);
      return;
    }

    FastStorage.removeUserBoardRole(boardRole);
    response.setStatus(OK);
    return;
  }
}
