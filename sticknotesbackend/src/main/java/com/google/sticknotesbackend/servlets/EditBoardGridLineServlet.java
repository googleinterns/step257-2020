package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.sticknotesbackend.AuthChecker;
import com.google.sticknotesbackend.enums.Permission;
import com.google.sticknotesbackend.exceptions.PayloadValidationException;
import com.google.sticknotesbackend.models.BoardGridLine;
import com.google.sticknotesbackend.models.Whiteboard;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Provides column/row edit functionality
 */
@WebServlet("api/edit-board-grid-lines/")
public class EditBoardGridLineServlet extends AppAbstractServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // validate request
    JsonObject jsonPayload = JsonParser.parseReader(request.getReader()).getAsJsonObject();
    try {
      String[] requiredFields = {"id"};
      validateRequestData(jsonPayload, response, requiredFields);
    } catch (PayloadValidationException ex) {
      // if exception was thrown, send error message to client
      badRequest(ex.getMessage(), response);
      return;
    }
    // load edited line form the datastore
    BoardGridLine editedLine = ofy().load().type(BoardGridLine.class).id(jsonPayload.get("id").getAsLong()).now();
    if (editedLine == null) {
      badRequest("No line with given id exists", response);
      return;
    }
    // check if user has enough permission to modify the line
    Permission perm = AuthChecker.boardAccessPermission(editedLine.boardId);
    if (!perm.equals(Permission.GRANTED)) {
      handleBadPermission(perm, response);
      return;
    }
    // apply changes to the line
    if (jsonPayload.has("title")) {
      editedLine.title = jsonPayload.get("title").getAsString();
    }
    if (jsonPayload.has("rangeStart")) {
      editedLine.rangeStart = jsonPayload.get("rangeStart").getAsInt();
    }
    if (jsonPayload.has("rangeEnd")) {
      editedLine.rangeEnd = jsonPayload.get("rangeEnd").getAsInt();
    }
    // load board and check that edited line doesn't overlap with any other line
    Whiteboard board = ofy().load().type(Whiteboard.class).id(editedLine.boardId).now();
    // some simple validation
    if (editedLine.rangeStart < 0 || editedLine.rangeStart == editedLine.rangeEnd || editedLine.rangeEnd > board.cols) {
      badRequest("Invalid value for rangeEnd or rangeStart", response);
      return;
    }
    for (int i = 0; i < board.gridLines.size(); ++i) {
      // get line from reference
      BoardGridLine l = board.gridLines.get(i).get();
      // check if line overlaps with some other line
      if (!l.id.equals(editedLine.id) && l.overlapsWith(editedLine)) {
        try {
          // overlaps, so throw bad request
          badRequest("new " + editedLine.type + " intersects with already existing", response);
          return;
        } catch (IOException ignored) {
          return;
        }
      }
    }
    // if no overlapping, save modified line
    ofy().save().entity(editedLine).now();
    Gson gson = new Gson();
    response.getWriter().print(gson.toJson(editedLine));
  }
}
