package com.google.sticknotesbackend.servlets;
import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.gson.Gson;
import com.google.sticknotesbackend.models.Whiteboard;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A specific servlet for board editing. Because Jetty server does not support PATCH method,
 * and we need to differentiate edit endpoint from create endpoint, we create a new endpoint for editing that has
 * another URI.
 */
@WebServlet("api/edit-board/")
public class EditBoardServlet extends BoardAbstractServlet {
  /**
   * Edits a board, for now only title editing is supported, returns an updated board.
   * The JSON payload must include field "id" and a set of editable fields with updated values.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = getBoardGsonParser();
    Whiteboard editedBoard = gson.fromJson(request.getReader(), Whiteboard.class);
    if (editedBoard.id == null) {
      // the payload for this method must have board id
      response.getWriter().println("No id in request");
      response.sendError(BAD_REQUEST);
      return;
    }
    if (editedBoard.title == null) {
      // if title is not initialized, it means it wasn't send, throw bad request
      response.getWriter().println("Invalid field edit attempt");
      response.sendError(BAD_REQUEST);
      return;
    }
    // get board that is to be edited from datastore
    Whiteboard board = ofy().load().type(Whiteboard.class).id(editedBoard.id).now();
    if (board == null) {
      // if board is null, it means there is no board with such id in the datastore
      response.getWriter().println("No board with the given id");
      response.sendError(BAD_REQUEST);
      return;
    }
    // update entity fields
    board.title = editedBoard.title;
    ofy().save().entity(board).now();
    // return updated board
    response.getWriter().println(gson.toJson(board));
  }
}

