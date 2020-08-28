package com.google.sticknotesbackend.servlets;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@WebServlet("api/login-url/")
public class LoginUrlServlet extends AppAbstractServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      String loginUrl = userService.createLoginURL("/boards");
      response.setStatus(OK);
      response.setContentType("text/html");
      response.getWriter().println(loginUrl);
      return;
    }
    // if user is already logged in - redirect them to boards list
    response.sendRedirect("/boards");
  }
}
