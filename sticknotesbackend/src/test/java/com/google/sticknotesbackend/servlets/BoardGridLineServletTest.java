package com.google.sticknotesbackend.servlets;
import static com.google.common.truth.Truth.assertThat;
import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.sticknotesbackend.enums.BoardGridLineType;
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

public class BoardGridLineServletTest extends NotesboardTestBase {
  private BoardGridLineServlet boardGridLineServlet;

  @Before
  public void setUp() throws Exception {
    // parent logic of setting up objectify
    super.setUp();
    // Set up a fake HTTP request
    when(mockRequest.getContentType()).thenReturn("application/json");
    // Set up a fake HTTP response
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    boardGridLineServlet = new BoardGridLineServlet();
  }

  @Test
  public void testCreateGridLineSuccessWithValidPayload() throws IOException, ServletException {
    // prepare mock data
    Whiteboard board = createBoard();
    User boardUser = createUserSafe();
    createRole(board, boardUser, Role.USER);
    // log user in
    logIn(boardUser);
    // construct payload
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("boardId", board.id);
    jsonObject.addProperty("rangeStart", 0);
    jsonObject.addProperty("rangeEnd", 2);
    jsonObject.addProperty("title", "value");
    jsonObject.addProperty("type", "column");
    // prepare request
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));
    // do a request
    boardGridLineServlet.doPost(mockRequest, mockResponse);
    // check that entity was created
    JsonObject body = JsonParser.parseString(responseWriter.toString()).getAsJsonObject();
    assertThat(body.has("id")).isTrue();
    // load line with the given id from datastore
    BoardGridLine savedLine = ofy().load().type(BoardGridLine.class).id(body.get("id").getAsLong()).now();
    assertThat(savedLine).isNotNull();
    // check that all necessary fields are present in the response
    assertThat(body.get("type").getAsString()).isEqualTo(BoardGridLineType.COLUMN.toString());
    assertThat(body.get("rangeStart").getAsInt()).isEqualTo(0);
    assertThat(body.get("rangeEnd").getAsInt()).isEqualTo(2);
    assertThat(body.get("title").getAsString()).isEqualTo("value");
    Long savedLineId = savedLine.id;
    // check that this line is added to the board
    Whiteboard boardWithNewLine = ofy().load().type(Whiteboard.class).id(board.id).now();
    // generate a list of ids of BoardGridLine objects stored in the board and check that saved one is there
    assertThat(boardWithNewLine.gridLines.stream().map(lineRef -> lineRef.get().id).toArray())
        .asList()
        .contains(savedLineId);
  }

  @Test
  public void testDeleteSuccessWithValidLineId() throws IOException, ServletException {
    // prepare mock data
    Whiteboard board = createBoard();
    User boardUser = createUserSafe();
    createRole(board, boardUser, Role.USER);
    // log user in
    logIn(boardUser);
    BoardGridLine line = createBoardGridLine(board.id);
    board.gridLines.add(Ref.create(line));
    // save updated board
    ofy().save().entity(board).now();
    // set line id as request param
    when(mockRequest.getParameter("id")).thenReturn(Long.toString(line.id));
    // do request
    boardGridLineServlet.doDelete(mockRequest, mockResponse);
    // check that no content was set
    verify(mockResponse).setStatus(NO_CONTENT);
    // check that line was really deleted from datastore
    assertThat(ofy().load().type(BoardGridLine.class).id(line.id).now()).isNull();
    // check that line was deleted from board
    assertThat(ofy()
                   .load()
                   .type(Whiteboard.class)
                   .id(board.id)
                   .now()
                   .gridLines.stream()
                   .filter(lineRef -> lineRef.get().id.equals(line.id))
                   .toArray())
        .isEmpty();
  }
}
