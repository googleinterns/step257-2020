/**
 * Copyright 2020 Google LLC
 */
package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.sticknotesbackend.AuthChecker;
import com.google.sticknotesbackend.FastStorage;
import com.google.sticknotesbackend.enums.BoardGridLineType;
import com.google.sticknotesbackend.enums.Permission;
import com.google.sticknotesbackend.exceptions.PayloadValidationException;
import com.google.sticknotesbackend.models.BoardGridLine;
import com.google.sticknotesbackend.models.Whiteboard;
import com.googlecode.objectify.Ref;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for creating/deleting board row/columns names
 */
@WebServlet("/api/board-grid-lines/")
public class BoardGridLineServlet extends AppAbstractServlet {
  /**
   * Creates a new board column/row for the board with the give param
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // validate request
    JsonObject jsonPayload = JsonParser.parseReader(request.getReader()).getAsJsonObject();
    try {
      String[] requiredFields = {"rangeStart", "rangeEnd", "title", "type", "boardId"};
      validateRequestData(jsonPayload, response, requiredFields);
    } catch (PayloadValidationException ex) {
      // if exception was thrown, send error message to client
      badRequest(ex.getMessage(), response);
      return;
    }
    Gson gson = new Gson();
    BoardGridLine line = gson.fromJson(jsonPayload, BoardGridLine.class);
    // parse type
    line.type = BoardGridLineType.valueOf(jsonPayload.get("type").getAsString().toUpperCase());
    // check if user has enougn permission to access the board
    Permission perm = AuthChecker.boardAccessPermission(line.boardId);
    if (!perm.equals(Permission.GRANTED)) {
      handleBadPermission(perm, response);
      return;
    }
    // load board and check that new line doesn't overlap with any other line
    Whiteboard board = ofy().load().type(Whiteboard.class).id(line.boardId).now();
    for(int i = 0; i < board.gridLines.size(); i++){
      // get line from reference
      BoardGridLine l = board.gridLines.get(i).get();
      // check if line overlaps with some other line
      if (l.overlapsWith(line)) {
        try {
          // overlaps, so throw bad request
          badRequest("new " + line.type + " intersects with already existing", response);
          return;
        } catch (IOException ignored) {}
      }
    }
    // if no overlapping, create a line and add it to the board
    line.id = ofy().save().entity(line).now().getId();
    board.gridLines.add(Ref.create(line));
    // save the board
    FastStorage.updateBoard(board);
    // return new line to the client
    response.getWriter().print(gson.toJson(line));
  }

  /**
   * Deletes a board line (row/column)
   */
  @Override
  public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String lineToDeleteId = request.getParameter("id");
    if (lineToDeleteId == null) {
      badRequest("Missing line url param", response);
      return;
    }
    // get BoardGridLine with the given id
    BoardGridLine lineToDelete = ofy().load().type(BoardGridLine.class).id(Long.parseLong(lineToDeleteId)).now();
    if (lineToDelete == null) {
      badRequest("No line with given id exists", response);
      return;
    }
    // check user has enough permission to delete the line
    Permission perm = AuthChecker.boardAccessPermission(lineToDelete.boardId);
    if (!perm.equals(Permission.GRANTED)) {
      handleBadPermission(perm, response);
      return;
    }
    // load board
    Whiteboard board = ofy().load().type(Whiteboard.class).id(lineToDelete.boardId).now();
    // remove line from board
    board.gridLines.removeIf(lineRef -> {
      if (lineRef.get() != null) {
        return lineRef.get().id.equals(lineToDelete.id);
      }
      return false;
    });
    FastStorage.updateBoard(board);
    // remove line itself
    ofy().delete().entity(lineToDelete).now();
    // send no content
    response.setStatus(NO_CONTENT);
  }
}
