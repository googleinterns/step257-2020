/**
 * Copyright 2020 Google LLC
 */
package com.google.sticknotesbackend.servlets;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import java.io.StringWriter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.sticknotesbackend.ActiveUsersManager;
import com.google.sticknotesbackend.enums.Role;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.UserBoardRole;
import com.google.sticknotesbackend.models.Whiteboard;

import java.io.IOException;
import java.io.PrintWriter;

public class ActiveUsersServletTest extends NotesboardTestBase {
  ActiveUsersServlet activeUsersServlet;

  @Before
  public void setUp() throws Exception {
    super.setUp();

    activeUsersServlet = new ActiveUsersServlet();

    when(mockRequest.getContentType()).thenReturn("application/json");
    // Set up a fake HTTP response
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
  }

  @Test
  public void oneActiveUser() throws IOException{
    User user = createUserSafe();
    Whiteboard board = createBoard();
    UserBoardRole role = createRole(board, user, Role.ADMIN);
    logIn(user);

    ActiveUsersManager.updateUserActivity(board.id, (long)0, System.currentTimeMillis()-activeUsersServlet.activityTimeMillis);
    ActiveUsersManager.updateUserActivity(board.id, (long)1, System.currentTimeMillis()-activeUsersServlet.activityTimeMillis-1);
    ActiveUsersManager.updateUserActivity(board.id, (long)2, System.currentTimeMillis()-activeUsersServlet.activityTimeMillis-1000);

    when(mockRequest.getParameter("id")).thenReturn(board.id.toString());

    activeUsersServlet.doGet(mockRequest, mockResponse);

    Gson gson = new Gson();
    JsonObject actualResponse = gson.fromJson(responseWriter.getBuffer().toString(), JsonObject.class);
    JsonArray arrayOfActiveUsers = actualResponse.get("activeUsers").getAsJsonArray();
    
    //only user that made a request should be determined as active
    assertFalse(arrayOfActiveUsers.contains(gson.toJsonTree(0)));
    assertTrue(arrayOfActiveUsers.contains(gson.toJsonTree(role.id)));
    assertFalse(arrayOfActiveUsers.contains(gson.toJsonTree(1)));
    assertFalse(arrayOfActiveUsers.contains(gson.toJsonTree(2)));
  }

  @Test
  public void threeActiveUser() throws IOException{
    User user = createUserSafe();
    Whiteboard board = createBoard();
    UserBoardRole role = createRole(board, user, Role.ADMIN);
    logIn(user);

    ActiveUsersManager.updateUserActivity(board.id, (long)0, System.currentTimeMillis());
    ActiveUsersManager.updateUserActivity(board.id, (long)1, System.currentTimeMillis());
    ActiveUsersManager.updateUserActivity(board.id, (long)2, System.currentTimeMillis()-activeUsersServlet.activityTimeMillis-1000);

    when(mockRequest.getParameter("id")).thenReturn(board.id.toString());

    activeUsersServlet.doGet(mockRequest, mockResponse);

    Gson gson = new Gson();
    JsonObject actualResponse = gson.fromJson(responseWriter.getBuffer().toString(), JsonObject.class);
    JsonArray arrayOfActiveUsers = actualResponse.get("activeUsers").getAsJsonArray();
    
    //only user that made a request should be determined as active
    assertTrue(arrayOfActiveUsers.contains(gson.toJsonTree(0)));
    assertTrue(arrayOfActiveUsers.contains(gson.toJsonTree(role.id)));
    assertTrue(arrayOfActiveUsers.contains(gson.toJsonTree(1)));
    assertFalse(arrayOfActiveUsers.contains(gson.toJsonTree(2)));
  }

  @Test
  public void updateUserActivity() throws IOException{
    User user = createUserSafe();
    Whiteboard board = createBoard();
    UserBoardRole role = createRole(board, user, Role.ADMIN);
    logIn(user);

    ActiveUsersManager.updateUserActivity(board.id, (long)0, System.currentTimeMillis()-activeUsersServlet.activityTimeMillis-1000);
    ActiveUsersManager.updateUserActivity(board.id, (long)0, System.currentTimeMillis());

    when(mockRequest.getParameter("id")).thenReturn(board.id.toString());

    activeUsersServlet.doGet(mockRequest, mockResponse);

    Gson gson = new Gson();
    JsonObject actualResponse = gson.fromJson(responseWriter.getBuffer().toString(), JsonObject.class);
    JsonArray arrayOfActiveUsers = actualResponse.get("activeUsers").getAsJsonArray();
    
    //only user that made a request should be determined as active
    assertTrue(arrayOfActiveUsers.contains(gson.toJsonTree(0)));
    assertTrue(arrayOfActiveUsers.contains(gson.toJsonTree(role.id)));
  }
}
