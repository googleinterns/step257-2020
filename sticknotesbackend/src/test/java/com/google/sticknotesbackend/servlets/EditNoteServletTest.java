package com.google.sticknotesbackend.servlets;

import static com.google.common.truth.Truth.assertThat;
import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.mockito.Mockito.when;

import com.google.gson.JsonObject;
import com.google.sticknotesbackend.enums.Role;
import com.google.sticknotesbackend.models.Note;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.UserBoardRole;
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

/**
 * Unit tests for EditNoteServlet
 */
public class EditNoteServletTest extends NotesboardTestBase {
  private EditNoteServlet editNoteServlet;

  @Before
  public void setUp() throws Exception {
    // parent logic of setting up objectify
    super.setUp();
    // Set up a fake HTTP request
    when(mockRequest.getContentType()).thenReturn("application/json");
    // Set up a fake HTTP response
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    editNoteServlet = new EditNoteServlet();
  }

  @Test
  public void testNoteEditSuccessWithValidPayload() throws IOException, ServletException {
    // create a mock note
    Note note = createNote();
    // create board
    Whiteboard board = createBoard();
    // creating mock user and log-in
    User user = createUserSafe();
    board.setCreator(user);
    note.setCreator(user);
    // save updated note
    note.id = ofy().save().entity(note).now().getId();
    board.notes.add(Ref.create(note));
    // save updated board
    ofy().save().entity(board).now();
    createRole(board, user, Role.ADMIN);
    // log user in
    logIn(user);
    // generate an updated payload
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("id", note.id);
    jsonObject.addProperty("color", "#000000");
    jsonObject.addProperty("content", "dummy content");
    jsonObject.addProperty("x", 2);
    jsonObject.addProperty("y", 1);
    // prepare mocked request
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));
    // do request
    editNoteServlet.doPost(mockRequest, mockResponse);
    // check that response has updated fields
    assertThat(responseWriter.toString()).contains("dummy content");
    assertThat(responseWriter.toString()).contains("#000000");
    // check that note from response is equal to note in the datastore
    Note savedNote = ofy().load().type(Note.class).id(note.id).now();
    assertThat(responseWriter.toString()).isEqualTo(editNoteServlet.getNoteGsonParser().toJson(savedNote));
  }
}
