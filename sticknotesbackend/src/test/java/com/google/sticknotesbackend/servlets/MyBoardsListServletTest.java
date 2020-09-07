package com.google.sticknotesbackend.servlets;

import static com.google.common.truth.Truth.assertThat;
import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.sticknotesbackend.enums.Role;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.UserBoardRole;
import com.google.sticknotesbackend.models.Whiteboard;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletException;
import org.junit.Before;
import org.junit.Test;

public class MyBoardsListServletTest extends NotesboardTestBase {
  private MyBoardsListServlet myBoardsListServlet;

  @Before
  public void setUp() throws Exception {
    // parent logic of setting up objectify
    super.setUp();
    // Set up a fake HTTP request
    when(mockRequest.getContentType()).thenReturn("application/json");
    // Set up a fake HTTP response
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    myBoardsListServlet = new MyBoardsListServlet();
  }

  @Test
  public void testMyBoardsGetSuccess() throws IOException, ServletException {
    // create 3 board, 1 user and 2 user roles
    User boardsCreator = new User("randomuser@google.com", "googler");
    boardsCreator.id = ofy().save().entity(boardsCreator).now().getId();
    Whiteboard board1 = new Whiteboard("Test board 1");
    board1.setCreator(boardsCreator);
    board1.id = ofy().save().entity(board1).now().getId();
    Whiteboard board2 = new Whiteboard("Test board 1");
    board2.setCreator(boardsCreator);
    board2.id = ofy().save().entity(board2).now().getId();
    Whiteboard board3 = new Whiteboard("Test board 1");
    board3.setCreator(boardsCreator);
    board3.id = ofy().save().entity(board3).now().getId();
    User user = new User("googler@google.com", "nick");
    user.googleAccId = "10";
    user.id = ofy().save().entity(user).now().getId();
    // only add user to the first and second board
    UserBoardRole role1 = new UserBoardRole(Role.USER, board1, user);
    role1.id = ofy().save().entity(role1).now().getId();
    UserBoardRole role2 = new UserBoardRole(Role.USER, board2, user);
    role2.id = ofy().save().entity(role2).now().getId();
    // log user in
    logIn(user);
    // do a request
    myBoardsListServlet.doGet(mockRequest, mockResponse);
    // expect to get board 1 and board 2 in the request
    Gson gson = myBoardsListServlet.getBoardPreviewGsonParser();
    JsonArray expectedResponse = new JsonArray();
    expectedResponse.add(gson.toJsonTree(board1));
    expectedResponse.add(gson.toJsonTree(board2));
    assertThat(responseWriter.toString()).isEqualTo(expectedResponse.toString());
  }

  @Test
  public void testMyBoardFailsIfNotAuthenticated() throws ServletException, IOException {
    myBoardsListServlet.doGet(mockRequest, mockResponse);
    verify(mockResponse).sendError(UNAUTHORIZED);
  }
}
