/**
 * Copyright 2020 Google LLC
 */
package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import com.google.gson.JsonObject;
import com.google.sticknotesbackend.enums.Role;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.UserBoardRole;
import com.google.sticknotesbackend.models.Whiteboard;
import com.googlecode.objectify.Key;

import org.junit.Before;
import org.junit.Test;

public class EditRoleServletTest extends NotesboardTestBase {

  private EditRoleServlet editRoleServlet;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    clearDatastore();

    ofy().clear(); // after loading data to datastore clearing cache once again

    editRoleServlet = new EditRoleServlet();

    when(mockRequest.getContentType()).thenReturn("application/json");
    // Set up a fake HTTP response
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
  }

  @Test
  public void ownerEditsUserTest() throws IOException {
    User owner = createUserSafe();
    User user = createUserSafe();
    logIn(owner);

    Whiteboard board = createBoard();

    UserBoardRole userRole = createRole(board, user, Role.USER);
    createRole(board, owner, Role.OWNER);

    // generate an updated payload
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("roleId", userRole.id);
    jsonObject.addProperty("boardId", board.id);
    jsonObject.addProperty("newRole", Role.ADMIN.toString());
    // prepare mocked request
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));

    editRoleServlet.doPost(mockRequest, mockResponse);

    verify(mockResponse).setStatus(OK);
    Role expectedRole = Role.ADMIN;
    Key<Whiteboard> boardKey = Key.create(Whiteboard.class, board.id);
    Role actualRole = ofy().load().type(UserBoardRole.class).ancestor(boardKey)
        .filterKey(Key.create(boardKey, UserBoardRole.class, userRole.id)).first().now().role;

    assertThat(expectedRole).isEqualTo(actualRole);
  }

  @Test
  public void adminEditsUserTest() throws IOException {
    User admin = createUserSafe();
    User user = createUserSafe();
    logIn(admin);

    Whiteboard board = createBoard();

    UserBoardRole userRole = createRole(board, user, Role.USER);
    createRole(board, admin, Role.ADMIN);

    // generate an updated payload
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("roleId", userRole.id);
    jsonObject.addProperty("boardId", board.id);
    jsonObject.addProperty("newRole", Role.ADMIN.toString());
    // prepare mocked request
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));

    editRoleServlet.doPost(mockRequest, mockResponse);

    verify(mockResponse).setStatus(OK);
    Role expectedRole = Role.ADMIN;
    Key<Whiteboard> boardKey = Key.create(Whiteboard.class, board.id);
    Role actualRole = ofy().load().type(UserBoardRole.class).ancestor(boardKey)
        .filterKey(Key.create(boardKey, UserBoardRole.class, userRole.id)).first().now().role;

    assertThat(expectedRole).isEqualTo(actualRole);
  }

  @Test
  public void adminEditsAdminTest() throws IOException {
    User admin = createUserSafe();
    User admin2 = createUserSafe();
    logIn(admin);

    Whiteboard board = createBoard();

    UserBoardRole userRole = createRole(board, admin2, Role.ADMIN);
    createRole(board, admin, Role.ADMIN);

    // generate an updated payload
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("roleId", userRole.id);
    jsonObject.addProperty("boardId", board.id);
    jsonObject.addProperty("newRole", Role.USER.toString());
    // prepare mocked request
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));

    editRoleServlet.doPost(mockRequest, mockResponse);

    verify(mockResponse).sendError(FORBIDDEN);
    Role expectedRole = Role.ADMIN;
    Key<Whiteboard> boardKey = Key.create(Whiteboard.class, board.id);
    Role actualRole = ofy().load().type(UserBoardRole.class).ancestor(boardKey)
        .filterKey(Key.create(boardKey, UserBoardRole.class, userRole.id)).first().now().role;

    assertThat(expectedRole).isEqualTo(actualRole);
  }
}
