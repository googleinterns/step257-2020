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

@WebServlet("api/login-url/")
public class LoginUrlServlet extends AppAbstractServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      String loginUrl = userService.createLoginURL("/boards");
      response.setContentType("application/json");
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("url", loginUrl);
      response.getWriter().println(jsonObject.toString());
      return;
    }
    forbidden(response);
  }
}
