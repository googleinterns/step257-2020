/**
 * Notesboard
 * Board API servlet
 */
package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.sticknotesbackend.enums.Role;
import com.google.sticknotesbackend.exceptions.PayloadValidationException;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.UserBoardRole;
import com.google.sticknotesbackend.models.Whiteboard;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Implements the following endpoints: POST - create a board GET with url param
 * "id" - retrieve a board
 */
@WebServlet("api/board/")
public class BoardServlet extends BoardAbstractServlet {
  /**
   * Retrieves a board with the given url param "id"
   */
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
      response.getWriter().print(gson.toJson(board));
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
    // convert request payload to a json object and validate it
    JsonObject jsonPayload = new JsonParser().parse(request.getReader()).getAsJsonObject();
    try {
      String[] requiredFields = { "title" };
      validateRequestData(jsonPayload, response, requiredFields);
    } catch (PayloadValidationException ex) {
      // if exception was thrown, send error message to client
      badRequest(ex.getMessage(), response);
      return;
    }
    // construct a gson that uses custom Whiteboard serializer
    Gson gson = getBoardGsonParser();
    Whiteboard board = gson.fromJson(jsonPayload, Whiteboard.class);
    board.creationDate = System.currentTimeMillis();
    // for now I create a dummy user entity, later user entity will be retrieved
    // from datastore
    User dummyUser = new User("nickname", "googler@google.com");
    ofy().save().entity(dummyUser).now();
    board.setCreator(dummyUser);
    board.rows = 4;
    board.cols = 6;
    // when the board is saved, get the auto generated id and assign to the board
    // field
    board.id = ofy().save().entity(board).now().getId();
    // automatically adding user with role ADMIN(will be changed to OWNER)
    UserBoardRole userBoardRole = new UserBoardRole(Role.ADMIN, board, dummyUser);
    ofy().save().entity(userBoardRole).now();
    // return JSON of the new created board
    response.getWriter().print(gson.toJson(board));
    // set 204 created status codes
    response.setStatus(CREATED);
  }
}
