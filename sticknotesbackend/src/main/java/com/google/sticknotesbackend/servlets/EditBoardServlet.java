package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.sticknotesbackend.AuthChecker;
import com.google.sticknotesbackend.FastStorage;
import com.google.sticknotesbackend.JsonParsers;
import com.google.sticknotesbackend.enums.Permission;
import com.google.sticknotesbackend.exceptions.PayloadValidationException;
import com.google.sticknotesbackend.models.BoardGridLine;
import com.google.sticknotesbackend.models.Note;
import com.google.sticknotesbackend.models.Whiteboard;
import com.googlecode.objectify.Ref;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A specific servlet for board editing. Because Jetty server does not support
 * PATCH method, and we need to differentiate edit endpoint from create
 * endpoint, we create a new endpoint for editing that has another URI.
 */
@WebServlet("api/edit-board/")
public class EditBoardServlet extends AppAbstractServlet {
  /**
   * Edits a board, for now only title editing is supported, returns an updated
   * board. The JSON payload must include field "id" and a set of editable fields
   * with updated values.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // convert request payload to a json object and validate it
    JsonObject jsonPayload = JsonParser.parseReader(request.getReader()).getAsJsonObject();
    try {
      String[] requiredFields = {"id"};
      validateRequestData(jsonPayload, response, requiredFields);
    } catch (PayloadValidationException ex) {
      // if exception was thrown, send error message to client
      badRequest(ex.getMessage(), response);
      return;
    }
    // construct a gson object that uses custom Whiteboard serializer
    Gson gson = JsonParsers.getBoardGsonParser();
    Whiteboard editedBoard = gson.fromJson(jsonPayload, Whiteboard.class);
    // get board that is to be edited from datastore
    Whiteboard board = ofy().load().type(Whiteboard.class).id(editedBoard.id).now();
    if (board == null) {
      // if board is null, it means there is no board with such id in the datastore
      badRequest("No board with the given id", response);
      return;
    }
    // check if user has enough permissions to modify the board
    Permission perm = AuthChecker.boardModifyPermission(board);
    if (!perm.equals(Permission.GRANTED)) {
      handleBadPermission(perm, response);
      return;
    }
    // update entity fields
    if (editedBoard.title != null) {
      board.title = editedBoard.title;
    }

    // run this code only if board is resized
    if ((editedBoard.cols != -1 && editedBoard.cols != board.cols) ||
        (editedBoard.rows != -1 && editedBoard.rows != board.rows)) {
      if (editedBoard.rows != -1) {
        if (editedBoard.rows < 1) {
          badRequest("Can not resize to < 1 rows", response);
          return;
        }
        board.rows = editedBoard.rows;
      }
      if (editedBoard.cols != -1) {
        if (editedBoard.cols < 1) {
          badRequest("Can not resize to < 1 cols", response);
          return;
        }
        board.cols = editedBoard.cols;
      }
      // check if resized board can fit all existing board notes
      // find if there are notes that has x or y coordinate bigger or equal to than
      // new cols or rows value
      for (Ref<Note> noteRef : board.notes) {
        Note boardNote = noteRef.get();
        if (boardNote.x >= board.cols || boardNote.y >= board.rows) {
          badRequest("Resizing will delete some notes", response);
          return;
        }
      }
      // delete column titles if there are ones starting further than new end
      Set<Ref<BoardGridLine>> linesToRemove = board.gridLines.stream()
                                        .filter(lineRef -> lineRef.get().rangeStart > board.cols)
                                        .collect(Collectors.toSet());
      // remove lines from the list                                        
      board.gridLines.removeIf(lineRef -> linesToRemove.contains(lineRef));
      // remove lines from the datastore
      linesToRemove.forEach(lineRef -> ofy().delete().entity(lineRef.get()).now());
      // shrink column titles if there are ones that overflow
      for (Ref<BoardGridLine> lineRef : board.gridLines) {
        BoardGridLine line = lineRef.get();
        if (line.rangeEnd > board.cols) {
          line.rangeEnd = board.cols;
          ofy().save().entity(line).now();
        }
      }
    }
    FastStorage.updateBoard(board);
    // return updated board
    response.getWriter().print(gson.toJson(board));
  }
}
