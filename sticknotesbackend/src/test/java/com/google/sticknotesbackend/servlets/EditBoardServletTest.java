package com.google.sticknotesbackend.servlets;

import static com.google.common.truth.Truth.assertThat;
import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.JsonObject;
import com.google.sticknotesbackend.enums.Role;
import com.google.sticknotesbackend.models.Note;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.Whiteboard;
import com.googlecode.objectify.Ref;
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
  }

  @Test
  public void testBoardEditSuccessWithValidPayload() throws Exception {
    String newBoardTitle = "New board title";
    // create board firstly
    Whiteboard board = createBoard();
    // creating mock user and log-in
    User user = createUserSafe();
    board.setCreator(user);
    // save updated the board
    ofy().save().entity(board).now();
    createRole(board, user, Role.ADMIN);
    // log user in
    logIn(user);
    // mock request payload
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("title", newBoardTitle);
    jsonObject.addProperty("id", board.id);
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));
    // do request
    editBoardServlet.doPost(mockRequest, mockResponse);
    String responseString = responseWriter.toString();
    // check if new board title is in the response
    assertThat(responseString.contains(newBoardTitle));
    // check if datastore entity really updated
    Whiteboard storedBoard = ofy().load().type(Whiteboard.class).id(board.id).now();
    assertThat(storedBoard.title).isEqualTo(newBoardTitle);
  }

  @Test
  public void testBoardEditFailsWithUnexistingId() throws IOException {
    User user = createUserSafe();
    // log user in
    logIn(user);
    // call post with board.id = "-1"
    // mock request payload
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("title", "a title");
    jsonObject.addProperty("id", "-1");
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));
    editBoardServlet.doPost(mockRequest, mockResponse);
    // check that bad request error was generated
    verify(mockResponse).sendError(BAD_REQUEST);
  }

  @Test
  public void testBoardResizeSuccessWithValidRowsAndColsValues() throws Exception {
    // create board firstly
    Whiteboard board = createBoard();
    // creating mock user and log-in
    User user = createUserSafe();
    board.setCreator(user);
    createRole(board, user, Role.ADMIN);
    // log user in
    logIn(user);
    // add some notes
    Note note = createNote();
    note.setCreator(user);
    note.boardId = board.id;
    ofy().save().entity(note).now();
    // add the note to the board object
    board.notes.add(Ref.create(note));
    // save updated board
    ofy().save().entity(board).now();
    // mock request payload
    JsonObject jsonObject = new JsonObject();
    // set unexisting property "newTitle"
    jsonObject.addProperty("id", board.id);
    jsonObject.addProperty("rows", 5);
    jsonObject.addProperty("cols", 5);
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));
    // do request
    editBoardServlet.doPost(mockRequest, mockResponse);
    // check that ofy really stored values 5,5
    board = ofy().load().type(Whiteboard.class).id(board.id).now();
    assertThat(board.rows).isEqualTo(5);
    assertThat(board.cols).isEqualTo(5);
  }

  @Test
  public void testBoardResizeFailsAsNotesGetDeleted() throws Exception {
    // create board firstly
    Whiteboard board = createBoard();
    // creating mock user and log-in
    User user = createUserSafe();
    board.setCreator(user);
    createRole(board, user, Role.ADMIN);
    // log user in
    logIn(user);
    // add some notes on the 4th row
    Note note = createNote();
    note.y = 4; // note is on the 4th row
    // save the note
    ofy().save().entity(note).now();
    // add the note to the board object
    board.notes.add(Ref.create(note));
    // save the updated board
    ofy().save().entity(board).now();
    // mock request payload
    JsonObject jsonObject = new JsonObject();
    // set unexisting property "newTitle"
    jsonObject.addProperty("id", board.id);
    jsonObject.addProperty("rows", 3);
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));
    // do request
    editBoardServlet.doPost(mockRequest, mockResponse);
    // check that bad request error was generated
    verify(mockResponse).sendError(BAD_REQUEST);
  }
}
