package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.sticknotesbackend.JsonParsers;
import com.google.sticknotesbackend.enums.Role;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.UserBoardRole;
import com.google.sticknotesbackend.models.Whiteboard;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import org.junit.Before;
import org.junit.Test;

public class UserListServletTest extends NotesboardTestBase {

  private UserListServlet userListServlet;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    clearDatastore();

    ofy().clear(); // after loading data to datastore clearing cache once again

    userListServlet = new UserListServlet();

    when(mockRequest.getContentType()).thenReturn("application/json");
    // Set up a fake HTTP response
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
  }

  @Test
  public void getBoardNotAuthorized() throws IOException {
    userListServlet.doGet(mockRequest, mockResponse);
    // verify response status
    verify(mockResponse).sendError(UNAUTHORIZED);
  }

  @Test
  public void testBoard1Key() throws IOException {
    User user1 = createUserSafe();
    User user2 = createUserSafe();
    User user3 = createUserSafe();
    User user4 = createUserSafe();

    Whiteboard board1 = createBoard();
    Whiteboard board2 = createBoard();
    Whiteboard board3 = createBoard();

    UserBoardRole userBoardRole1 = createRole(board1, user1, Role.ADMIN);
    UserBoardRole userBoardRole2 = createRole(board1, user2, Role.ADMIN);
    UserBoardRole userBoardRole3 = createRole(board1, user3, Role.USER);
    UserBoardRole userBoardRole4 = createRole(board1, user4, Role.USER);
    UserBoardRole userBoardRole5 = createRole(board2, user3, Role.USER);
    UserBoardRole userBoardRole6 = createRole(board2, user4, Role.USER);

    ofy().clear(); // after loading data to datastore clearing cache once again

    logIn(user2);

    when(mockRequest.getParameter("id")).thenReturn(board1.id.toString());

    userListServlet.doGet(mockRequest, mockResponse);

    // preparing expected response based on dataset initialized in datastore
    Gson gson = JsonParsers.getBoardRoleGsonParser();
    JsonArray expectedResponse = new JsonArray();
    expectedResponse.add(gson.toJsonTree(userBoardRole1));
    expectedResponse.add(gson.toJsonTree(userBoardRole2));
    expectedResponse.add(gson.toJsonTree(userBoardRole3));
    expectedResponse.add(gson.toJsonTree(userBoardRole4));

    // veryfing response
    verify(mockResponse).setContentType("application/json");
    verify(mockResponse).setStatus(OK);

    JsonArray actualResponse = gson.fromJson(responseWriter.getBuffer().toString(), JsonArray.class);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void testBoard2Key() throws IOException {
    User user1 = createUserSafe();
    User user2 = createUserSafe();
    User user3 = createUserSafe();
    User user4 = createUserSafe();

    Whiteboard board1 = createBoard();
    Whiteboard board2 = createBoard();
    Whiteboard board3 = createBoard();

    UserBoardRole userBoardRole1 = createRole(board1, user1, Role.ADMIN);
    UserBoardRole userBoardRole2 = createRole(board1, user2, Role.ADMIN);
    UserBoardRole userBoardRole3 = createRole(board1, user3, Role.USER);
    UserBoardRole userBoardRole4 = createRole(board1, user4, Role.USER);
    UserBoardRole userBoardRole5 = createRole(board2, user3, Role.USER);
    UserBoardRole userBoardRole6 = createRole(board2, user4, Role.USER);

    ofy().clear(); // after loading data to datastore clearing cache once again

    logIn(user1);

    when(mockRequest.getParameter("id")).thenReturn(board2.id.toString());

    userListServlet.doGet(mockRequest, mockResponse);

    // preparing expected response based on dataset initialized in datastore
    Gson gson = JsonParsers.getBoardRoleGsonParser();
    JsonArray expectedResponse = new JsonArray();
    expectedResponse.add(gson.toJsonTree(userBoardRole5));
    expectedResponse.add(gson.toJsonTree(userBoardRole6));

    // veryfing status
    verify(mockResponse).setContentType("application/json");
    verify(mockResponse).setStatus(OK);

    JsonArray actualResponse = gson.fromJson(responseWriter.getBuffer().toString(), JsonArray.class);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void testNotExistingBoard() throws IOException {
    // creating mock user and log-in
    User user = createUserSafe();
    logIn(user);

    when(mockRequest.getParameter("id")).thenReturn("-1");

    userListServlet.doGet(mockRequest, mockResponse);

    // veryfing status
    verify(mockResponse).sendError(BAD_REQUEST);
  }

  @Test
  public void testBoardExistsButNoUsers() throws IOException {
    // creating mock user and log-in
    User user = createUserSafe();
    logIn(user);

    Whiteboard board = createBoard();

    when(mockRequest.getParameter("id")).thenReturn(board.id.toString());

    userListServlet.doGet(mockRequest, mockResponse);

    Gson gson = JsonParsers.getBoardRoleGsonParser();
    JsonArray expectedResponse = new JsonArray();

    // veryfing status
    verify(mockResponse).setContentType("application/json");
    verify(mockResponse).setStatus(OK);

    JsonArray actualResponse = gson.fromJson(responseWriter.getBuffer().toString(), JsonArray.class);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void testAddUserNotAllowedUserAddsUser() throws IOException {
    // creating mock user and log-in
    User user = createUserSafe();
    logIn(user);

    Whiteboard board = createBoard();
    createRole(board, user, Role.USER);

    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("email", "test@google.com");
    jsonObject.addProperty("role", "user");

    when(mockRequest.getParameter("id")).thenReturn(board.id.toString());
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));

    userListServlet.doPost(mockRequest, mockResponse);

    // verify response status
    verify(mockResponse).sendError(FORBIDDEN);
  }

  @Test
  public void testAddUserNotAllowedUserAddsAdmin() throws IOException {
    // creating mock user and log-in
    // creating mock user and log-in
    User user = createUserSafe();
    logIn(user);

    Whiteboard board = createBoard();
    createRole(board, user, Role.USER);

    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("email", "test@google.com");
    jsonObject.addProperty("role", "admin");

    when(mockRequest.getParameter("id")).thenReturn(board.id.toString());
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));

    userListServlet.doPost(mockRequest, mockResponse);

    // verify response status
    verify(mockResponse).sendError(FORBIDDEN);
  }

  @Test
  public void testAddUserNotAllowedUserAddsOwner() throws IOException {
    // creating mock user and log-in
    User user = createUserSafe();
    logIn(user);

    Whiteboard board = createBoard();
    createRole(board, user, Role.USER);

    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("email", "test@google.com");
    jsonObject.addProperty("role", "owner");

    when(mockRequest.getParameter("id")).thenReturn(board.id.toString());
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));

    userListServlet.doPost(mockRequest, mockResponse);

    // verify response status
    verify(mockResponse).sendError(FORBIDDEN);
  }

  @Test
  public void testAddUserNotAllowedAdminAddsAdmin() throws IOException {
    // creating mock user and log-in
    User user = createUserSafe();
    logIn(user);

    Whiteboard board = createBoard();
    createRole(board, user, Role.ADMIN);

    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("email", "test@google.com");
    jsonObject.addProperty("role", "admin");

    when(mockRequest.getParameter("id")).thenReturn(board.id.toString());
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));

    userListServlet.doPost(mockRequest, mockResponse);

    // verify response status
    verify(mockResponse).sendError(FORBIDDEN);
  }

  @Test
  public void testAddUserNotAllowedAdminAddsOwner() throws IOException {
    User user = createUserSafe();
    logIn(user);

    Whiteboard board = createBoard();
    createRole(board, user, Role.ADMIN);

    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("email", "test@google.com");
    jsonObject.addProperty("role", "owner");

    when(mockRequest.getParameter("id")).thenReturn(board.id.toString());
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));

    userListServlet.doPost(mockRequest, mockResponse);

    // verify response status
    verify(mockResponse).sendError(FORBIDDEN);
  }

  @Test
  public void testOwnerAddUserToBoardUserExistsBoardExists() throws IOException {
    // creating mock user and log-in
    User user = createUserSafe();
    logIn(user);

    Whiteboard board = createBoard();
    createRole(board, user, Role.OWNER);

    User userToAdd = createUserSafe();

    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("email", userToAdd.email);
    jsonObject.addProperty("role", "user");

    when(mockRequest.getParameter("id")).thenReturn(board.id.toString());
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));

    userListServlet.doPost(mockRequest, mockResponse);

    Gson gson = JsonParsers.getBoardRoleGsonParser();

    // checking response status
    verify(mockResponse).setStatus(OK);

    // checking if data correctly in the datastore
    ofy().clear();
    UserBoardRole datastoreData = ofy().load().type(UserBoardRole.class).ancestor(board).filter("user", userToAdd)
        .filter("role", Role.USER).first().now();

    assertNotNull(datastoreData);

    // checking response value
    JsonElement expectedResponse = gson.toJsonTree(datastoreData, UserBoardRole.class);
    JsonElement actualResponse = gson.fromJson(responseWriter.getBuffer().toString(), JsonObject.class);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void testAdminAddUserToBoardUserExistsBoardExists() throws IOException {
    // creating mock user and log-in
    User user = createUserSafe();
    logIn(user);

    Whiteboard board = createBoard();
    createRole(board, user, Role.ADMIN);

    User userToAdd = createUserSafe();

    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("email", userToAdd.email);
    jsonObject.addProperty("role", "user");

    when(mockRequest.getParameter("id")).thenReturn(board.id.toString());
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));

    userListServlet.doPost(mockRequest, mockResponse);

    Gson gson = JsonParsers.getBoardRoleGsonParser();

    // checking response status
    verify(mockResponse).setStatus(OK);

    // checking if data correctly in the datastore
    ofy().clear();
    UserBoardRole datastoreData = ofy().load().type(UserBoardRole.class).ancestor(board).filter("user", userToAdd)
        .filter("role", Role.USER).first().now();

    assertNotNull(datastoreData);

    // checking response value
    JsonElement expectedResponse = gson.toJsonTree(datastoreData, UserBoardRole.class);
    JsonElement actualResponse = gson.fromJson(responseWriter.getBuffer().toString(), JsonObject.class);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void testAddUserToBoardUserAlreadyInTheBoardList() throws IOException {
    // creating mock user and log-in
    User user = createUserSafe();
    logIn(user);

    Whiteboard board = createBoard();
    createRole(board, user, Role.OWNER);

    User userAlreadyInTheBoard = createUserSafe();
    createRole(board, userAlreadyInTheBoard, Role.USER);

    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("email", userAlreadyInTheBoard.email);
    jsonObject.addProperty("role", "admin");

    when(mockRequest.getParameter("id")).thenReturn(board.id.toString());
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));

    userListServlet.doPost(mockRequest, mockResponse);

    // checking response status
    verify(mockResponse).sendError(BAD_REQUEST);
    // checking response value
    String actualResponse = responseWriter.getBuffer().toString();
    String expectedResponse = "User already in the list.\n";

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void testAddUserToBoardUserExistsBoardNotExists() throws IOException {
    // creating mock user and log-in
    User user = createUserSafe();
    logIn(user);

    Whiteboard board = createBoard();
    createRole(board, user, Role.OWNER);

    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("email", "email4@google.com");
    jsonObject.addProperty("role", Role.ADMIN.toString());

    when(mockRequest.getParameter("id")).thenReturn("-1");
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));

    userListServlet.doPost(mockRequest, mockResponse);

    // checking response status
    verify(mockResponse).sendError(BAD_REQUEST);
    // checking response value
    String actualResponse = responseWriter.getBuffer().toString();
    String expectedResponse = "Board with a given id not found.\n";

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void testAddUserToBoardUserNotExistsBoardExists() throws IOException {
    // creating mock user and log-in
    User user = createUserSafe();
    logIn(user);

    Whiteboard board = createBoard();
    createRole(board, user, Role.OWNER);

    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("email", "user6@google.com");
    jsonObject.addProperty("role", "admin");

    when(mockRequest.getParameter("id")).thenReturn(board.id.toString());
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));

    userListServlet.doPost(mockRequest, mockResponse);

    // checking response status
    verify(mockResponse).setStatus(OK);
    // checking response value
    /**
     * will later implement whole test
     */
  }

  @Test
  public void testAddUserToBoardUserNotExistsBoardNotExists() throws IOException {
    // creating mock user and log-in
    User user = createUserSafe();
    logIn(user);

    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("email", "user6@google.com");
    jsonObject.addProperty("role", "admin");

    when(mockRequest.getParameter("id")).thenReturn("-1");
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));

    userListServlet.doPost(mockRequest, mockResponse);

    // checking response status
    verify(mockResponse).sendError(BAD_REQUEST);
    // checking response value
    String actualResponse = responseWriter.getBuffer().toString();
    String expectedResponse = "Board with a given id not found.\n";

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void testDeleteRoleExists() throws IOException {
    // creating mock user and log-in
    User user = createUserSafe();
    logIn(user);

    Whiteboard board = createBoard();
    createRole(board, user, Role.OWNER);

    UserBoardRole roleToDelete = createRole(board, createUserSafe(), Role.USER);

    when(mockRequest.getParameter("id")).thenReturn(roleToDelete.id.toString());
    when(mockRequest.getParameter("board-id")).thenReturn(board.id.toString());

    userListServlet.doDelete(mockRequest, mockResponse);

    // checking response status
    verify(mockResponse).setStatus(OK);

    UserBoardRole datastoreData = ofy().load().type(UserBoardRole.class).id(roleToDelete.id).now();

    assertNull(datastoreData);
  }

  @Test
  public void testDeleteRoleNotExists() throws IOException {
    // creating mock user and log-in
    User user = createUserSafe();
    logIn(user);

    Whiteboard board = createBoard();
    createRole(board, user, Role.OWNER);

    Long roleId = (long) -1;
    when(mockRequest.getParameter("id")).thenReturn(roleId.toString());
    when(mockRequest.getParameter("board-id")).thenReturn(board.id.toString());

    userListServlet.doDelete(mockRequest, mockResponse);
    // checking response status
    verify(mockResponse).sendError(BAD_REQUEST);
    // checking response value
    String actualResponse = responseWriter.getBuffer().toString();
    String expectedResponse = "Role with a given id not found.\n";

    assertEquals(expectedResponse, actualResponse);
  }
}
