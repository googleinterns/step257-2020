package com.google.sticknotesbackend.servlets;

import java.io.IOException;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.sticknotesbackend.serializers.UserBoardRoleSerializer;
import com.google.sticknotesbackend.enums.Role;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.UserBoardRole;
import com.google.sticknotesbackend.models.Whiteboard;

@WebServlet("api/board/users/")
public class UserListServlet extends AppAbstractServlet {
  // with a given id it returns list of users of the board
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String boardIdParam = request.getParameter("id");
    if (boardIdParam != null) {
      Long boardId = Long.valueOf(boardIdParam);
      Whiteboard board = ofy().load().type(Whiteboard.class).id(boardId).now();
      if (board != null) {
        List<UserBoardRole> boardUsers = ofy().load().type(UserBoardRole.class).filter("board", board).list();
        Gson userBoardRoleParser = getBoardRoleGsonParser();

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
    String boardIdParam = request.getParameter("id");
    if (boardIdParam == null) {
      badRequest("Error while reading request param.", response);
      return;
    }

    Long boardId = Long.parseLong(boardIdParam);

    Gson gson = getBoardRoleGsonParser();
    JsonObject body = new JsonParser().parse(request.getReader()).getAsJsonObject();
    if (!body.has("email") || !body.has("role")) {
      badRequest("Request has to contain 'email' and 'role' property", response);
      return;
    }
    String email = body.get("email").getAsString();
    Role role = null;
    try {
      role = Role.valueOf(body.get("role").getAsString().toUpperCase());
    } catch (IllegalArgumentException e) {
      badRequest("Role has to be admin or user", response);
      return;
    }

    Whiteboard board = ofy().load().type(Whiteboard.class).id(boardId).now();
    if (board == null) {
      badRequest("Board with a given id not found.", response);
      return;
    }

    User user = ofy().load().type(User.class).filter("email", email).first().now();
    if (user == null) {
      user = new User(email, "---");
      ofy().save().entity(user).now();
    }

    UserBoardRole roleFromDatastore = ofy().load().type(UserBoardRole.class).filter("board", board).filter("user", user)
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

  @Override
  public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String boardRoleIdParam = request.getParameter("id");
    if (boardRoleIdParam == null) {
      badRequest("Error while reading request param.", response);
      return;
    }

    Long boardRoleId = Long.valueOf(boardRoleIdParam);

    UserBoardRole boardRole = ofy().load().type(UserBoardRole.class).id(boardRoleId).now();
    if (boardRole == null) {
      badRequest("Role with a given id not found.", response);
      return;
    }

    ofy().delete().entity(boardRole).now();
    response.setStatus(OK);
    return;
  }

  public Gson getBoardRoleGsonParser() {
    GsonBuilder gson = new GsonBuilder();
    gson.registerTypeAdapter(UserBoardRole.class, new UserBoardRoleSerializer());
    Gson parser = gson.create();
    return parser;
  }
}
