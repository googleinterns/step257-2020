package com.google.sticknotesbackend.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import com.google.gson.JsonArray;
import com.google.sticknotesbackend.models.Note;
import com.google.sticknotesbackend.models.UpdateQueryData;
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
    Note note1 = createNote();
    Note note2 = createNote();
    Note note3 = createNote();
    Note note4 = createNote();

    Gson gson = new Gson();
    JsonObject requestBody = new JsonObject();
    JsonArray requestArray = new JsonArray();
    requestArray.add(gson.toJsonTree(new UpdateQueryData(note1.id, note1.lastUpdated)));
    requestArray.add(gson.toJsonTree(new UpdateQueryData(note2.id, note2.lastUpdated)));
    requestArray.add(gson.toJsonTree(new UpdateQueryData(note3.id, note3.lastUpdated)));
    requestArray.add(gson.toJsonTree(new UpdateQueryData(note4.id, note4.lastUpdated)));

    requestBody.add("notes", requestArray);



    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody.toString())));

    notesUpdateServlet.doPost(mockRequest, mockResponse);
  }
}
