package com.google.sticknotesbackend.servlets;
import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.UserBoardRole;
import com.google.sticknotesbackend.models.Whiteboard;
import com.google.sticknotesbackend.serializers.WhiteboardPreviewSerializer;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Returns the list of boards available to the user
 */
@WebServlet("api/myboards/")
public class MyBoardsListServlet extends AppAbstractServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      unauthorized(response);
      return;
    }

    // get google id of the current user
    String googleAccId = userService.getCurrentUser().getUserId();
    // get the user with this id
    User user = ofy().load().type(User.class).filter("googleAccId", googleAccId).first().now();
    // get all UserBoardRoles where this user is
    List<UserBoardRole> userRoles = ofy().load().type(UserBoardRole.class).filter("user", user).list();
    JsonArray boardsJsonArray = new JsonArray();
    Gson gson = getBoardPreviewGsonParser();
    for (UserBoardRole role: userRoles) {
      boardsJsonArray.add(gson.toJsonTree(role.getBoard()));
    }
    response.getWriter().print(boardsJsonArray.toString());
  }

  public Gson getBoardPreviewGsonParser() {
    GsonBuilder gson = new GsonBuilder();
    gson.registerTypeAdapter(Whiteboard.class, new WhiteboardPreviewSerializer());
    Gson parser = gson.create();
    return parser;
  }
}
