package com.google.sticknotesbackend.servlets;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sticknotesbackend.models.User;
import com.google.gson.Gson;
import static com.googlecode.objectify.ObjectifyService.ofy;

@WebServlet("api/user/")
public class UserServlet extends AppAbstractServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    // if user is looged in than we update/or save for the first time email,
    // username and user id
    if (userService.isUserLoggedIn()) {
      User currentUser = ofy().load().type(User.class).filterKey("userId", userService.getCurrentUser().getUserId())
          .first().now();
      currentUser.email = userService.getCurrentUser().getUserId();
      currentUser.nickname = userService.getCurrentUser().getNickname();

      ofy().save().entity(currentUser).now();

      Gson gson = new Gson();
      response.setStatus(OK);
      response.setContentType("application/json");
      response.getWriter().println(gson.toJson(currentUser));
      return;
    }
    unauthenticated(response);
  }
}
