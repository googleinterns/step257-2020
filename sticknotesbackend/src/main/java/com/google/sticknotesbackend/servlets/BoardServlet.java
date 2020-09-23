/**
 * Notesboard
 * Board API servlet
 */
package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.sticknotesbackend.AuthChecker;
import com.google.sticknotesbackend.FastStorage;
import com.google.sticknotesbackend.JsonParsers;
import com.google.sticknotesbackend.enums.Permission;
import com.google.sticknotesbackend.enums.Role;
import com.google.sticknotesbackend.exceptions.PayloadValidationException;
import com.google.sticknotesbackend.models.Note;
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
public class BoardServlet extends AppAbstractServlet {
  /**
   * Retrieves a board with the given url param "id"
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String boardIdParam = request.getParameter("id");
    // optional language param
    if (boardIdParam != null) {
      long boardId = Long.parseLong(boardIdParam);
      Whiteboard board = ofy().load().type(Whiteboard.class).id(boardId).now();
      if (board == null) {
        badRequest("Board with this id doesn't exist", response);
        return;
      }

      // check if user can access the board
      Permission perm = AuthChecker.boardAccessPermission(boardId);
      if (!perm.equals(Permission.GRANTED)) {
        handleBadPermission(perm, response);
        return;
      }
      Gson gson = JsonParsers.getBoardGsonParser();
      response.getWriter().print(gson.toJson(board));
    } else {
      badRequest("No id parameter", response);
    }
  }

  @Override
  public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String boardIdParam = request.getParameter("id");
    if (boardIdParam != null) {
      long boardId = Long.parseLong(boardIdParam);
      Whiteboard board = ofy().load().type(Whiteboard.class).id(boardId).now();
      if (board == null) {
        badRequest("Board with this id doesn't exist", response);
        return;
      }
      // check if user has enough permissions to modify the board
      Permission perm = AuthChecker.boardDeletePermission(board);
      if (!perm.equals(Permission.GRANTED)) {
        handleBadPermission(perm, response);
        return;
      }
      // to delete board we need to delete all roles connected with that board, and
      // all notes that are on the board

      // deleting all roles
      ofy().delete().keys(ofy().load().ancestor(board).keys().list()).now();

      // deleting all notes which "boardId" property is equal to deleted board
      ofy().delete().entities(ofy().load().type(Note.class).filter("boardId", board.id).iterable()).now();
      // TODO delete rows and columns
      // deleting board itself
      FastStorage.removeBoard(board);

      response.setStatus(NO_CONTENT);
    } else {
      badRequest("No id parameter", response);
    }
  }

  /**
   * Creates a new board. Required field is "title"
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // authorization check
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      unauthorized(response);
      return;
    }
    // convert request payload to a json object and validate it
    JsonObject jsonPayload = JsonParser.parseReader(request.getReader()).getAsJsonObject();
    try {
      String[] requiredFields = { "title" };
      validateRequestData(jsonPayload, response, requiredFields);
    } catch (PayloadValidationException ex) {
      // if exception was thrown, send error message to client
      badRequest(ex.getMessage(), response);
      return;
    }
    // construct a gson that uses custom Whiteboard serializer
    Gson gson = JsonParsers.getBoardGsonParser();
    Whiteboard board = gson.fromJson(jsonPayload, Whiteboard.class);
    board.creationDate = System.currentTimeMillis();
    board.lastUpdated = System.currentTimeMillis();
    // at this point we can assume that users is logged in (so also present in
    // datastore)
    // get google id of the current user
    String googleAccId = userService.getCurrentUser().getUserId();
    // get the user with this id
    User user = ofy().load().type(User.class).filter("googleAccId", googleAccId).first().now();
    // ofy().save().entity(user).now();
    board.setCreator(user);
    board.rows = 4;
    board.cols = 6;
    // when the board is saved, get the auto generated id and assign to the board
    // field
    board.id = ofy().save().entity(board).now().getId();
    // automatically adding user with role OWNER
    UserBoardRole userBoardRole = new UserBoardRole(Role.OWNER, board, user);
    ofy().save().entity(userBoardRole).now();
    // return JSON of the new created board
    response.getWriter().print(gson.toJson(board));
    // set 201 created status codes
    response.setStatus(CREATED);
  }
}
