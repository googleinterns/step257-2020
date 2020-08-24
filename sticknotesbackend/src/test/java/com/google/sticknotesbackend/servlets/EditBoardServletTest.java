package com.google.sticknotesbackend.servlets;

import static com.google.common.truth.Truth.assertThat;
import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.JsonObject;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.Whiteboard;
import com.googlecode.objectify.ObjectifyService;
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
 * Unit tests for EditBoardServlet
 */
@RunWith(JUnit4.class)
public class EditBoardServletTest extends NotesboardTestBase {
  private EditBoardServlet editBoardServlet;

  @Before
  public void setUp() throws Exception {
    // parent logic of setting up objectify
    super.setUp();
    // Set up a fake HTTP request
    when(mockRequest.getContentType()).thenReturn("application/json");
    // Set up a fake HTTP response
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    editBoardServlet = new EditBoardServlet();
    editBoardServlet.init();
  }

  @Test
  public void testBoardEditSuccessWithValidPayload() throws Exception {
    String newBoardTitle = "New board title";
    // create board firstly
    Whiteboard board = getMockBoard();
    // when the board is saved, get the auto generated id and assign to board field
    board.id = ofy().save().entity(board).now().getId();
    // mock request payload
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("title", newBoardTitle);
    jsonObject.addProperty("id", board.id);
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));

    editBoardServlet.doPost(mockRequest, mockResponse);
    String responseString = responseWriter.toString();
    // check if new board title is in the response
    assertThat(responseString.contains(newBoardTitle));
    // check if datastore entity really updated
    Whiteboard storedBoard = ofy().load().type(Whiteboard.class).id(board.id).now();
    assertThat(storedBoard.title.equals(newBoardTitle));
  }

  @Test
  public void testBoardEditFailsWithUnexistingId() throws IOException {
    // create board entity
    Whiteboard board = getMockBoard();
    // when the board is saved, get the auto generated id and assign to board field
    board.id = ofy().save().entity(board).now().getId();
    // call get with board.id + 1
    // mock request payload
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("title", board.title);
    jsonObject.addProperty("id", board.id + 1);
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));
    editBoardServlet.doPost(mockRequest, mockResponse);
    // check that bad request error was generated
    verify(mockResponse).sendError(BAD_REQUEST);
  }

  @Test
  public void testBoardEditFailsWithInvalidPayload() throws Exception {
    String newBoardTitle = "New board title";
    // create board firstly
    Whiteboard board = getMockBoard();
    // when the board is saved, get the auto generated id and assign to board field
    board.id = ofy().save().entity(board).now().getId();
    // mock request payload
    JsonObject jsonObject = new JsonObject();
    // set unexisting property "newTitle"
    jsonObject.addProperty("newtTitle", newBoardTitle);
    jsonObject.addProperty("id", board.id);
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));

    editBoardServlet.doPost(mockRequest, mockResponse);
    // check that bad request error was generated
    verify(mockResponse).sendError(BAD_REQUEST);
  }
}
