package com.google.sticknotesbackend.servlets;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@WebServlet("api/logout-url/")
public class LogoutUrlServlet extends AppAbstractServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String logoutUrl = userService.createLogoutURL("/");
      response.setStatus(OK);
      response.setContentType("text/html");
      response.getWriter().println(logoutUrl);
      return;
    }

    unauthorized(response);
  }
}
