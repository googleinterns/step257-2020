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
    User boardsCreator = createUser();
    Whiteboard board1 = createBoard();
    board1.setCreator(boardsCreator);
    ofy().save().entity(board1).now();
    Whiteboard board2 = createBoard();
    board2.setCreator(boardsCreator);
    ofy().save().entity(board2).now();
    Whiteboard board3 = createBoard();
    board3.setCreator(boardsCreator);
    ofy().save().entity(board3).now();
    User user = createUser();
    // only add user to the first and second board
    createRole(board1, user, Role.USER);
    createRole(board2, user, Role.USER);
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
