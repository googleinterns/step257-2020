/**
 * Copyright 2020 Google LLC
 */
package com.google.sticknotesbackend.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.JsonObject;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("api/logout-url/")
public class LogoutUrlServlet extends AppAbstractServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String logoutUrl = userService.createLogoutURL("/");
      response.setStatus(OK);
      response.setContentType("application/json");
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("url", logoutUrl);
      response.getWriter().println(jsonObject.toString());
      return;
    }

    unauthorized(response);
  }
}
