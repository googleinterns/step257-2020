package com.google.sticknotesbackend.servlets;

import static com.google.common.truth.Truth.assertThat;
import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.JsonObject;
import com.google.sticknotesbackend.enums.Role;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.Whiteboard;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Unit tests for BoardServlet.
 */
@RunWith(JUnit4.class)
public class BoardServletTest extends NotesboardTestBase {
  private BoardServlet boardServlet;

  @Before
  public void setUp() throws Exception {
    // parent logic of setting up objectify
    super.setUp();
    // Set up a fake HTTP request
    when(mockRequest.getContentType()).thenReturn("application/json");
    // Set up a fake HTTP response
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    boardServlet = new BoardServlet();
  }

  @Test
  public void testBoardCreateSuccessWithValidPayload() throws Exception {
    // creating mock user and log-in
    User user = createUser();
    logIn(user);
    // create mocked request
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("title", "Board title");
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));

    boardServlet.doPost(mockRequest, mockResponse);
    // verify response status
    verify(mockResponse).setStatus(CREATED);
    assertThat(responseWriter.toString()).contains("Board title");
  }

  @Test
  public void testBoardCreateNotAuthorized() throws Exception {
    boardServlet.doPost(mockRequest, mockResponse);
    // verify response status
    verify(mockResponse).sendError(UNAUTHORIZED);
  }

  @Test
  public void testBoardRetrieveNotAuthorized() throws Exception {
    boardServlet.doGet(mockRequest, mockResponse);
    // verify response status
    verify(mockResponse).sendError(UNAUTHORIZED);
  }

  @Test
  public void testBoardRetrieveSuccessWithValidBoardId() throws Exception {
    // create board firstly
    Whiteboard board = createBoard();
    // create mock user
    User user = createUser();
    // set board creator
    board.setCreator(user);
    // when the board is saved, get the auto generated id and assign to board field
    // save board with updated user field
    ofy().save().entity(board).now();
    // finally create a role
    createRole(board, user, Role.ADMIN);
    // log user in
    logIn(user);
    // mock request get parameter
    when(mockRequest.getParameter("id")).thenReturn(Long.toString(board.id));
    // do request
    boardServlet.doGet(mockRequest, mockResponse);
    // check if the response is exactly the same as generated by Gson.toJson(board)
    assertThat(responseWriter.toString()).isEqualTo(boardServlet.getBoardGsonParser().toJson(board));
  }

  @Test
  public void testBoardRetrieveFailsIfUserNotInTheBoardUsersList() throws Exception {
    // create board firstly
    Whiteboard board = createBoard();
    // create mock users
    User boardCreator = createUser();
    User user = createUser();
    board.setCreator(boardCreator);
    // save updated board
    ofy().save().entity(board);
    // log in user who is not in the list of board users
    logIn(user);
    // try to retrieve a board
    // mock request get parameter
    when(mockRequest.getParameter("id")).thenReturn(Long.toString(board.id));
    // do request
    boardServlet.doGet(mockRequest, mockResponse);
    // check that forbidden error was thrown
    verify(mockResponse).sendError(FORBIDDEN);
  }
  @Test
  public void testBoardRetrieveFailsWithUnexistingId() throws IOException {
    // creating mock user and log-in
    User user = createUser();
    // log user in
    logIn(user);
    // call get with board.id = "-1"
    // mock request get parameter
    when(mockRequest.getParameter("id")).thenReturn("-1");
    // do request
    boardServlet.doGet(mockRequest, mockResponse);
    // check that bad request error was generated
    verify(mockResponse).sendError(BAD_REQUEST);
  }

  @Test
  public void testBoardCreateFailsWithInvalidPayload() throws Exception {
    // creating mock user and log-in
    User user = createUser();
    logIn(user);
    // create mocked request
    JsonObject jsonObject = new JsonObject();
    // add unexisting property
    jsonObject.addProperty("boardtitle", "Board title");
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));
    // do request
    boardServlet.doPost(mockRequest, mockResponse);
    // verify response status
    verify(mockResponse).sendError(BAD_REQUEST);
    assertThat(responseWriter.toString()).isEqualTo("title field must be set\n");
  }
}
