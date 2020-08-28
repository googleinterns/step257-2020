/**
 * Notesboard
 * Board API servlet
 */
package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.gson.Gson;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.Whiteboard;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Implements the following endpoints:
 * POST - create a board
 * GET with url param "id" - retrieve a board
 */
@WebServlet("api/board/")
public class BoardServlet extends BoardAbstractServlet {
  /**
   * Retrieves a board with the given url param "id"
   * */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String boardIdParam = request.getParameter("id");
    if (boardIdParam != null) {
      long boardId = Long.parseLong(boardIdParam);
      Whiteboard board = ofy().load().type(Whiteboard.class).id(boardId).now();
      if (board == null) {
        response.getWriter().println("Board with this id doesn't exist");
        response.sendError(BAD_REQUEST);
        return;
      }
      Gson gson = getBoardGsonParser();
      response.getWriter().println(gson.toJson(board));
    } else {
      response.getWriter().println("No id parameter");
      response.sendError(BAD_REQUEST);
    }
  }

  /**
   * Creates a new board. Required field is "title"
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = getBoardGsonParser();
    Whiteboard board = gson.fromJson(request.getReader(), Whiteboard.class);
    board.creationDate = System.currentTimeMillis();
    // for now I create a dummy user entity, later user entity will be retrieved from datastore
    board.setCreator(new User("randomid", "googler@google.com", "nickname"));
    board.rows = 4;
    board.cols = 6;
    // when the board is saved, get the auto generated id and assign to the board field
    board.id = ofy().save().entity(board).now().getId();
    // return JSON of the new created board
    response.getWriter().println(gson.toJson(board));
    // set 204 created status codes
    response.setStatus(CREATED);
  }
}
