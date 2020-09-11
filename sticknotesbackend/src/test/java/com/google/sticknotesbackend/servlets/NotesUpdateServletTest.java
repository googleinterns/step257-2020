package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.sticknotesbackend.FastStorage;
import com.google.sticknotesbackend.JsonParsers;
import com.google.sticknotesbackend.enums.Role;
import com.google.sticknotesbackend.models.Note;
import com.google.sticknotesbackend.models.UpdateQueryData;
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

public class NotesUpdateServletTest extends NotesboardTestBase {
  private NotesUpdateServlet notesUpdateServlet;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    clearDatastore();

    notesUpdateServlet = new NotesUpdateServlet();

    when(mockRequest.getContentType()).thenReturn("application/json");
    // Set up a fake HTTP response
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
  }

  @Test
  public void testRemovedNote() throws IOException {
    Gson gson = JsonParsers.getNoteGsonParser();
    User user = createUserSafe();
    Whiteboard board = createBoard();
    createRole(board, user, Role.USER);
    logIn(user);

    JsonObject requestBody = new JsonObject();
    JsonArray requestArray = new JsonArray();

    requestArray.add(gson.toJsonTree(new UpdateQueryData((long)2354, (long)0)));

    requestBody.add("notes", requestArray);
    requestBody.addProperty("boardId", board.id.toString());

    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody.toString())));

    notesUpdateServlet.doPost(mockRequest, mockResponse);

    JsonArray expectedRemovedNotes = new JsonArray();
    expectedRemovedNotes.add(2354);

    JsonObject expectedResponse = new JsonObject();
    expectedResponse.add("removedNotes", expectedRemovedNotes);
    expectedResponse.add("updatedNotes", new JsonArray());
    // veryfing response
    verify(mockResponse).setContentType("application/json");
    verify(mockResponse).setStatus(OK);

    JsonObject actualResponse = gson.fromJson(responseWriter.getBuffer().toString(), JsonObject.class);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void testLastUpdatedNull() throws IOException {
    Gson gson = JsonParsers.getNoteGsonParser();
    User user = createUserSafe();
    Whiteboard board = createBoard();
    createRole(board, user, Role.USER);
    logIn(user);
    User creator = createUserSafe();
    Note note = createNoteNotSave();
    note.setCreator(creator);
    FastStorage.updateNote(note);
    board.notes.add(Ref.create(note));
    FastStorage.updateBoard(board);

    JsonObject requestBody = new JsonObject();
    JsonArray requestArray = new JsonArray();

    requestArray.add(gson.toJsonTree(new UpdateQueryData(note.id, (long)0)));

    requestBody.add("notes", requestArray);
    requestBody.addProperty("boardId", board.id.toString());

    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody.toString())));

    notesUpdateServlet.doPost(mockRequest, mockResponse);

    JsonArray expectedUpdatedNotes = new JsonArray();
    expectedUpdatedNotes.add(gson.toJsonTree(note));

    JsonObject expectedResponse = new JsonObject();
    expectedResponse.add("removedNotes", new JsonArray());
    expectedResponse.add("updatedNotes", expectedUpdatedNotes);
    // veryfing response
    verify(mockResponse).setContentType("application/json");
    verify(mockResponse).setStatus(OK);

    JsonObject actualResponse = gson.fromJson(responseWriter.getBuffer().toString(), JsonObject.class);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void noUpdatesTest() throws IOException {
    Gson gson = JsonParsers.getNoteGsonParser();
    User user = createUserSafe();
    Whiteboard board = createBoard();
    createRole(board, user, Role.USER);
    logIn(user);

    Note note1 = createNoteWithCreatorAndDates();
    Note note2 = createNoteWithCreatorAndDates();
    Note note3 = createNoteWithCreatorAndDates();
    Note note4 = createNoteWithCreatorAndDates();

    board.notes.add(Ref.create(note1));
    board.notes.add(Ref.create(note2));
    board.notes.add(Ref.create(note3));
    board.notes.add(Ref.create(note4));

    JsonObject requestBody = new JsonObject();
    JsonArray requestArray = new JsonArray();


    requestArray.add(gson.toJsonTree(new UpdateQueryData(note1.id, note1.lastUpdated)));
    requestArray.add(gson.toJsonTree(new UpdateQueryData(note2.id, note2.lastUpdated)));
    requestArray.add(gson.toJsonTree(new UpdateQueryData(note3.id, note3.lastUpdated)));
    requestArray.add(gson.toJsonTree(new UpdateQueryData(note4.id, note4.lastUpdated)));

    requestBody.add("notes", requestArray);
    requestBody.addProperty("boardId", board.id.toString());

    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody.toString())));

    notesUpdateServlet.doPost(mockRequest, mockResponse);

    JsonObject expectedResponse = new JsonObject();
    expectedResponse.add("removedNotes", new JsonArray());
    expectedResponse.add("updatedNotes", new JsonArray());
    // veryfing response
    verify(mockResponse).setContentType("application/json");
    verify(mockResponse).setStatus(OK);

    JsonObject actualResponse = gson.fromJson(responseWriter.getBuffer().toString(), JsonObject.class);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void oneUpdateTest() throws IOException {
    Gson gson = JsonParsers.getNoteGsonParser();
    User user = createUserSafe();
    Whiteboard board = createBoard();
    createRole(board, user, Role.USER);
    logIn(user);

    Note note1 = createNoteWithCreatorAndDatesNotSave();
    Note note2 = createNoteWithCreatorAndDatesNotSave();
    Note note3 = createNoteWithCreatorAndDatesNotSave();
    Note note4 = createNoteWithCreatorAndDatesNotSave();

    FastStorage.updateNote(note1);
    FastStorage.updateNote(note2);
    FastStorage.updateNote(note3);
    FastStorage.updateNote(note4);

    board.notes.add(Ref.create(note1));
    board.notes.add(Ref.create(note2));
    board.notes.add(Ref.create(note3));
    board.notes.add(Ref.create(note4));

    FastStorage.updateBoard(board);

    JsonObject requestBody = new JsonObject();
    JsonArray requestArray = new JsonArray();


    requestArray.add(gson.toJsonTree(new UpdateQueryData(note1.id, note1.lastUpdated)));
    requestArray.add(gson.toJsonTree(new UpdateQueryData(note2.id, note2.lastUpdated)));
    requestArray.add(gson.toJsonTree(new UpdateQueryData(note3.id, note3.lastUpdated)));
    requestArray.add(gson.toJsonTree(new UpdateQueryData(note4.id, note4.lastUpdated)));

    note4.lastUpdated+=1;

    FastStorage.updateNote(note4);

    requestBody.add("notes", requestArray);
    requestBody.addProperty("boardId", board.id.toString());

    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody.toString())));

    notesUpdateServlet.doPost(mockRequest, mockResponse);

    JsonArray expectedUpdatedNotes = new JsonArray();
    expectedUpdatedNotes.add(gson.toJsonTree(note4));

    JsonObject expectedResponse = new JsonObject();
    expectedResponse.add("removedNotes", new JsonArray());
    expectedResponse.add("updatedNotes", expectedUpdatedNotes);

    // veryfing response
    verify(mockResponse).setContentType("application/json");
    verify(mockResponse).setStatus(OK);

    JsonObject actualResponse = gson.fromJson(responseWriter.getBuffer().toString(), JsonObject.class);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void newNoteTest() throws IOException {
    Gson gson = JsonParsers.getNoteGsonParser();
    
    User user = createUserSafe();
    Whiteboard board = createBoard();
    createRole(board, user, Role.USER);
    logIn(user);

    Note note1 = createNoteWithCreatorAndDates();
    Note note2 = createNoteWithCreatorAndDates();
    Note newNote = createNoteWithCreatorAndDates();

    board.notes.add(Ref.create(note1));
    board.notes.add(Ref.create(note2));
    board.notes.add(Ref.create(newNote));

    JsonObject requestBody = new JsonObject();
    JsonArray requestArray = new JsonArray();


    requestArray.add(gson.toJsonTree(new UpdateQueryData(note1.id, note1.lastUpdated)));
    requestArray.add(gson.toJsonTree(new UpdateQueryData(note2.id, note2.lastUpdated)));

    requestBody.add("notes", requestArray);
    requestBody.addProperty("boardId", board.id.toString());

    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody.toString())));

    notesUpdateServlet.doPost(mockRequest, mockResponse);

    JsonArray expectedUpdatedNotes = new JsonArray();
    expectedUpdatedNotes.add(gson.toJsonTree(newNote));

    JsonObject expectedResponse = new JsonObject();
    expectedResponse.add("removedNotes", new JsonArray());
    expectedResponse.add("updatedNotes", expectedUpdatedNotes);
    // veryfing response
    verify(mockResponse).setContentType("application/json");
    verify(mockResponse).setStatus(OK);

    JsonObject actualResponse = gson.fromJson(responseWriter.getBuffer().toString(), JsonObject.class);
    assertEquals(expectedResponse, actualResponse);
  }
}
