/**
 * Copyright 2020 Google LLC
 */
package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static com.google.common.truth.Truth.assertThat;

import com.google.appengine.repackaged.com.google.gson.JsonObject;
import com.google.sticknotesbackend.enums.Role;
import com.google.sticknotesbackend.models.BoardGridLine;
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

public class EditBoardGridLineServletTest extends NotesboardTestBase {
  private EditBoardGridLineServlet editBoardGridLineServlet;
  @Before
  public void setUp() throws Exception {
    // parent logic of setting up objectify
    super.setUp();
    // Set up a fake HTTP request
    when(mockRequest.getContentType()).thenReturn("application/json");
    // Set up a fake HTTP response
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    editBoardGridLineServlet = new EditBoardGridLineServlet();
  }

  @Test
  public void testEditWithValidPayloadWorks() throws IOException, ServletException {
    // create board
    Whiteboard board = createBoard();
    User boardUser = createUserSafe();
    // add user to a list of board users
    createRole(board, boardUser, Role.USER);
    BoardGridLine line = createBoardGridLine(board.id);
    // add line to a list of board lines
    board.gridLines.add(Ref.create(line));
    // log user in
    logIn(boardUser);

    // construct a request
    JsonObject payload = new JsonObject();
    payload.addProperty("title", "NEW");
    payload.addProperty("rangeStart", 1);
    payload.addProperty("rangeEnd", 3);
    payload.addProperty("id", line.id);
    // write payload to request
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(payload.toString())));
    // do request
    this.editBoardGridLineServlet.doPost(mockRequest, mockResponse);
    // check that line was really edited
    line = ofy().load().type(BoardGridLine.class).id(line.id).now();
    assertThat(line.title).isEqualTo(payload.get("title").getAsString());
    assertThat(line.rangeStart).isEqualTo(payload.get("rangeStart").getAsInt());
    assertThat(line.rangeEnd).isEqualTo(payload.get("rangeEnd").getAsInt());
  }

  @Test
  public void testEditLineDetectsOverlap() throws IOException, ServletException {
    // create board
    Whiteboard board = createBoard();
    User boardUser = createUserSafe();
    // add user to a list of board users
    createRole(board, boardUser, Role.USER);
    BoardGridLine line = createBoardGridLine(board.id);
    BoardGridLine lineThatWillOverlap = createBoardGridLine(board.id);
    lineThatWillOverlap.rangeStart = 3;
    lineThatWillOverlap.rangeEnd = 4;
    ofy().save().entity(lineThatWillOverlap).now();
    // add line to a list of board lines
    board.gridLines.add(Ref.create(line));
    board.gridLines.add(Ref.create(lineThatWillOverlap));
    // log user in
    logIn(boardUser);
    // construct a request
    JsonObject payload = new JsonObject();
    payload.addProperty("rangeStart", 1);
    payload.addProperty("rangeEnd", 3);
    payload.addProperty("id", lineThatWillOverlap.id);
    // write payload to request
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(payload.toString())));
    // do request
    this.editBoardGridLineServlet.doPost(mockRequest, mockResponse);
    // check that line was not edited
    BoardGridLine notEditedLine = ofy().load().type(BoardGridLine.class).id(lineThatWillOverlap.id).now();
    assertThat(notEditedLine.rangeStart).isEqualTo(lineThatWillOverlap.rangeStart);
    assertThat(notEditedLine.rangeEnd).isEqualTo(lineThatWillOverlap.rangeEnd);
    verify(mockResponse).sendError(BAD_REQUEST);
  }
}
