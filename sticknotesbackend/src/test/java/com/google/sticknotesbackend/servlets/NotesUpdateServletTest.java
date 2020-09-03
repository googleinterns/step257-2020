package com.google.sticknotesbackend.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import com.google.gson.JsonArray;
import com.google.sticknotesbackend.models.Note;
import com.google.sticknotesbackend.models.UpdateQueryData;
import com.google.sticknotesbackend.models.Whiteboard;
import com.googlecode.objectify.Ref;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import static org.mockito.Mockito.when;

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
  public void noUpdatesTest() throws IOException {
    Whiteboard board = createBoard();

    Note note1 = createNoteWithCreatorAndDates();
    Note note2 = createNoteWithCreatorAndDates();
    Note note3 = createNoteWithCreatorAndDates();
    Note note4 = createNoteWithCreatorAndDates();

    board.notes.add(Ref.create(note1));
    board.notes.add(Ref.create(note2));
    board.notes.add(Ref.create(note3));
    board.notes.add(Ref.create(note4));

    Gson gson = new Gson();
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
  }

  @Test
  public void oneUpdateTest() throws IOException {
    Whiteboard board = createBoard();

    Note note1 = createNoteWithCreatorAndDates();
    Note note2 = createNoteWithCreatorAndDates();
    Note note3 = createNoteWithCreatorAndDates();
    Note note4 = createNoteWithCreatorAndDates();

    board.notes.add(Ref.create(note1));
    board.notes.add(Ref.create(note2));
    board.notes.add(Ref.create(note3));
    board.notes.add(Ref.create(note4));

    Gson gson = new Gson();
    JsonObject requestBody = new JsonObject();
    JsonArray requestArray = new JsonArray();


    requestArray.add(gson.toJsonTree(new UpdateQueryData(note1.id, note1.lastUpdated)));
    requestArray.add(gson.toJsonTree(new UpdateQueryData(note2.id, note2.lastUpdated)));
    requestArray.add(gson.toJsonTree(new UpdateQueryData(note3.id, note3.lastUpdated)));
    requestArray.add(gson.toJsonTree(new UpdateQueryData(note4.id, note4.lastUpdated)));

    note4.lastUpdated+=1;

    requestBody.add("notes", requestArray);
    requestBody.addProperty("boardId", board.id.toString());

    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody.toString())));

    notesUpdateServlet.doPost(mockRequest, mockResponse);
  }
}
