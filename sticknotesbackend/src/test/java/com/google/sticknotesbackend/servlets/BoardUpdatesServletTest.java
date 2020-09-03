package com.google.sticknotesbackend.servlets;

import static com.google.common.truth.Truth.assertThat;
import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.mockito.Mockito.when;

import com.google.gson.JsonObject;
import com.google.sticknotesbackend.enums.Role;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.Whiteboard;
import com.googlecode.objectify.Ref;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import javax.servlet.ServletException;
import org.junit.Before;
import org.junit.Test;

public class BoardUpdatesServletTest extends NotesboardTestBase {
  private BoardUpdatesServlet boardUpdatesServlet;
  @Before
  public void setUp() throws Exception {
    // parent logic of setting up objectify
    super.setUp();
    // Set up a fake HTTP request
    when(mockRequest.getContentType()).thenReturn("application/json");
    // Set up a fake HTTP response
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    boardUpdatesServlet = new BoardUpdatesServlet();
  }

  @Test
  public void testNoUpdatesSentIfTimestampsAreEqual() throws IOException, ServletException {
    Whiteboard board = createBoard();
    User user = createUserSafe();
    logIn(user);
    board.setCreator(user);
    ofy().save().entity(board).now();
    // allow user access the board
    createRole(board, user, Role.OWNER);
    JsonObject requestData = new JsonObject();
    // create payload
    requestData.addProperty("id", board.id);
    requestData.addProperty("lastUpdated", board.lastUpdated);
    // add payload to the mock request
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(requestData.toString())));
    // do request
    boardUpdatesServlet.doPost(mockRequest, mockResponse);
    assertThat(responseWriter.toString()).isEqualTo("{}");
  }

  @Test
  public void testUpdatesSentIfTimestampsAreNotEqual() throws IOException, ServletException {
    Whiteboard board = createBoard();
    board.notes.add(Ref.create(createNote()));
    board.notes.add(Ref.create(createNote()));
    User user = createUserSafe();
    logIn(user);
    board.setCreator(user);
    ofy().save().entity(board).now();
    // allow user access the board
    createRole(board, user, Role.OWNER);
    JsonObject requestData = new JsonObject();
    // create payload
    requestData.addProperty("id", board.id);
    requestData.addProperty("lastUpdated", board.lastUpdated - 1);
    // add payload to the mock request
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(requestData.toString())));
    // do request
    boardUpdatesServlet.doPost(mockRequest, mockResponse);
    assertThat(responseWriter.toString()).isEqualTo(boardUpdatesServlet.getBoardUpdateGsonParser().toJson(board));
  }
}
