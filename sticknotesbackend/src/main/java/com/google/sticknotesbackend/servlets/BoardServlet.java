/**
 * Notesboard
 * Board API servlet
 */
package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.Whiteboard;
import com.google.sticknotesbackend.serializers.WhiteboardSerializer;
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
@WebServlet("api/board/")
public class BoardServlet extends NotesboardAbstractServlet {
  private final int CREATED = 204;
  private final int BAD_REQUEST = 400;
  // retrieves a board with the given id
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

  // creates a new board
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = getBoardGsonParser();
    Whiteboard board = gson.fromJson(request.getReader(), Whiteboard.class);
    board.creationDate = System.currentTimeMillis();
    // for now I create a dummy user entity, later user entity will be retrieved from datastore
    board.setCreator(ofy().save().entity(new User("randomid", "googler@google.com", "nickname")).now());
    board.rows = 4;
    board.cols = 6;
    // when the board is saved, get the auto generated id and assign to the board field
    board.id = ofy().save().entity(board).now().getId();
    // return JSON of the new created board
    response.getWriter().println(gson.toJson(board));
    // set 204 created status codes
    response.setStatus(CREATED);
  }

  // edits a board, for now only title editing is supported, returns an updated board
  @Override
  public void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // load board with requested id from the database
    String boardIdParam = request.getParameter("id");
    if (boardIdParam != null) {
      // get board that is to be edited from datastore
      long boardId = Long.parseLong(boardIdParam);
      Whiteboard board = ofy().load().type(Whiteboard.class).id(boardId).now();
      if (board == null) {
        // if board is null, it means there is no board with such id in the datastore
        response.getWriter().println("Board with this id doesn't exist");
        response.sendError(BAD_REQUEST);
        return;
      }
      Gson gson = getBoardGsonParser();
      // get board object with edited fields
      Whiteboard editedBoard = gson.fromJson(request.getReader(), Whiteboard.class);
      if (editedBoard.title == null) {
        // if title is not initialized, it means it wasn't send, throw bad request
        response.getWriter().println("Invalid field edit attempt");
        response.sendError(BAD_REQUEST);
        return;
      }
      // update entity fields
      board.title = editedBoard.title;
      ofy().save().entity(board).now();
      // return updated board
      response.getWriter().println(gson.toJson(board));
    } else {
      response.getWriter().println("No id parameter");
      response.sendError(BAD_REQUEST);
    }
  }

  // generates a Gson object that uses custom WhiteboardSerializer
  public Gson getBoardGsonParser() {
    GsonBuilder gson = new GsonBuilder();
    gson.registerTypeAdapter(Whiteboard.class, new WhiteboardSerializer());
    Gson parser = gson.create();
    return parser;
  }
}
