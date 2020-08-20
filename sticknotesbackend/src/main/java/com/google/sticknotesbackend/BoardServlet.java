/**
 * Notesboard
 * Board API servlet
 */
package com.google.sticknotesbackend;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Implements the following endpoints:
 * POST - create a board
 * GET with id - retrieve a board
 * PATCH with id - edit a board
 */
@WebServlet("api/boards/")
public class BoardServlet extends NotesboardAbstractServlet {

  // returns a board with the given id
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
  }

  // creates a new board
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Gson gson = new Gson();
    Board board = gson.fromJson(req.getReader(), Board.class);
    board.creationDate = Long.toString(System.currentTimeMillis());
    board.creator = "googler@google.com";
    board.rows = 4;
    board.cols = 6;
    Key<Board> savedBoardKey = ofy().save().entity(board).now();
    board.id = savedBoardKey.getId();
    resp.getWriter().println(gson.toJson(board));
  }

  // edits a board
  @Override
  public void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
  }
}
