package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import com.google.gson.JsonElement;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.sticknotesbackend.enums.Role;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.UserBoardRole;
import com.google.sticknotesbackend.models.Whiteboard;
import java.io.BufferedReader;

import org.junit.Before;
import org.junit.Test;

public class UserListServletTest extends NotesboardTestBase {

  UserBoardRole userBoardRole1;
  UserBoardRole userBoardRole2;
  UserBoardRole userBoardRole3;
  UserBoardRole userBoardRole4;
  UserBoardRole userBoardRole5;
  UserBoardRole userBoardRole6;

  User user1;
  User user2;
  User user3;
  User user4;

  Whiteboard board1;
  Whiteboard board2;
  Whiteboard board3;

  private UserListServlet userListServlet;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    clearDatastore();
    ofy().clear(); //clearing Objectify cache
    // filling datastore with board and few users
    user1 = createUser();
    user2 = createUser();
    user3 = createUser();
    user4 = createUser();

    board1 = createBoard();
    board2 = createBoard();
    board3 = createBoard();

  
    userBoardRole1 = createRole(board1, user1, Role.ADMIN);
    userBoardRole2 = createRole(board1, user2, Role.ADMIN);
    userBoardRole3 = createRole(board1, user3, Role.USER);
    
    userBoardRole4 = createRole(board1, user4, Role.USER);
    userBoardRole5 = createRole(board2, user3, Role.USER);
    userBoardRole6 = createRole(board2, user4, Role.USER);

    ofy().clear(); //after loading data to datastore clearing cache once again

    userListServlet = new UserListServlet();

    when(mockRequest.getContentType()).thenReturn("application/json");
    // Set up a fake HTTP response
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
  }

  @Test
  public void testBoard1Key() throws IOException {
    when(mockRequest.getParameter("id")).thenReturn(board1.id.toString());

    userListServlet.doGet(mockRequest, mockResponse);

    //preparing expected response based on dataset initialized in datastore
    Gson gson = userListServlet.getBoardRoleGsonParser();
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
    when(mockRequest.getParameter("id")).thenReturn(board2.id.toString());

    userListServlet.doGet(mockRequest, mockResponse);

    //preparing expected response based on dataset initialized in datastore
    Gson gson = userListServlet.getBoardRoleGsonParser();
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
    Long boardId = (long) -1;

    when(mockRequest.getParameter("id")).thenReturn(boardId.toString());

    userListServlet.doGet(mockRequest, mockResponse);

    // veryfing status
    verify(mockResponse).sendError(BAD_REQUEST);
  }

  @Test
  public void testBoardExistsButNoUsers() throws IOException {
    when(mockRequest.getParameter("id")).thenReturn(board3.id.toString());

    userListServlet.doGet(mockRequest, mockResponse);

    Gson gson = userListServlet.getBoardRoleGsonParser();
    JsonArray expectedResponse = new JsonArray();

    // veryfing status
    verify(mockResponse).setContentType("application/json");
    verify(mockResponse).setStatus(OK);

    JsonArray actualResponse = gson.fromJson(responseWriter.getBuffer().toString(), JsonArray.class);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void testAddUserToBoardUserExistsBoardExists() throws IOException {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("email", user1.email);
    jsonObject.addProperty("role", "admin");

    when(mockRequest.getParameter("id")).thenReturn(board2.id.toString());
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));

    userListServlet.doPost(mockRequest, mockResponse);


    Gson gson = userListServlet.getBoardRoleGsonParser();

    // checking response status
    verify(mockResponse).setStatus(OK);

    // checking if data correctly in the datastore
    ofy().clear();
    UserBoardRole datastoreData = ofy().load().type(UserBoardRole.class).filter("board", board2).filter("user", user1)
        .filter("role", Role.ADMIN).first().now();

    assertNotNull(datastoreData);

    // checking response value
    JsonElement expectedResponse = gson.toJsonTree(datastoreData, UserBoardRole.class);
    JsonElement actualResponse = gson.fromJson(responseWriter.getBuffer().toString(), JsonObject.class);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void testAddUserToBoardUserAlreadyInTheBoardList() throws IOException {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("email", user1.email);
    jsonObject.addProperty("role", "admin");

    when(mockRequest.getParameter("id")).thenReturn(board1.id.toString());
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
    Long boardId = (long) -1;
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("email", user4.email);
    jsonObject.addProperty("role", Role.ADMIN.toString());

    when(mockRequest.getParameter("id")).thenReturn(boardId.toString());
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
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("email", "user6@google.com");
    jsonObject.addProperty("role", "admin");

    when(mockRequest.getParameter("id")).thenReturn(board1.id.toString());
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
    Long boardId = (long) -1;

    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("email", "user6@google.com");
    jsonObject.addProperty("role", "admin");

    when(mockRequest.getParameter("id")).thenReturn(boardId.toString());
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
    when(mockRequest.getParameter("id")).thenReturn(userBoardRole1.id.toString());

    userListServlet.doDelete(mockRequest, mockResponse);

    // checking response status
    verify(mockResponse).setStatus(OK);

    UserBoardRole datastoreData = ofy().load().type(UserBoardRole.class).id(userBoardRole1.id).now();

    assertNull(datastoreData);
  }

  @Test
  public void testDeleteRoleNotExists() throws IOException {
    Long roleId = (long) -1;
    when(mockRequest.getParameter("id")).thenReturn(roleId.toString());

    userListServlet.doDelete(mockRequest, mockResponse);
    // checking response status
    verify(mockResponse).sendError(BAD_REQUEST);
    // checking response value
    String actualResponse = responseWriter.getBuffer().toString();
    String expectedResponse = "Role with a given id not found.\n";

    assertEquals(expectedResponse, actualResponse);
  }
}
