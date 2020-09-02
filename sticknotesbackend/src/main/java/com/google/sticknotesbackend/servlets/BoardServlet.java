/**
 * Notesboard
 * Board API servlet
 */
package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.sticknotesbackend.AuthChecker;
import com.google.sticknotesbackend.Cacher;
import com.google.sticknotesbackend.JsonParsers;
import com.google.sticknotesbackend.enums.Permission;
import com.google.sticknotesbackend.enums.Role;
import com.google.sticknotesbackend.exceptions.PayloadValidationException;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.UserBoardRole;
import com.google.sticknotesbackend.models.Whiteboard;
import java.io.IOException;
import javax.cache.Cache;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;

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
    String languageCode = request.getParameter("lc");
    // optional last update param, this endpoint used for fetching updates as well
    String boardLastUpdate = request.getParameter("lu");
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
      response.setCharacterEncoding("UTF-8");
      // check if cache has board with the given id and language code
      Cacher.BoardCacheEntry cacheEntry = Cacher.getBoardFromCache(boardIdParam, languageCode);
      if (cacheEntry != null) {
        // if client is asking for an update and client's data is the same to server's data, send nothing in response
        if (boardLastUpdate != null && cacheEntry.lastUpdatedTimestamp.equals(boardLastUpdate)) {
          return;
        }
        // if client's data is different, send cached version
        response.getWriter().print(cacheEntry.board);
        return;
      }
      // if translate language is set, translate all notes
      if (languageCode != null) {
        Translate translate = TranslateOptions.getDefaultInstance().getService();
        board.notes.forEach(noteRef -> {
          // translate each note
          Translation translation =
              translate.translate(noteRef.get().content, Translate.TranslateOption.targetLanguage(languageCode));
          noteRef.get().content = translation.getTranslatedText();
        });
      }
      Gson gson = JsonParsers.getBoardGsonParser();
      String boardJson = gson.toJson(board);
      // save board to cache
      Cacher.storeBoardInCache(boardIdParam, boardJson, languageCode);
      response.getWriter().print(boardJson);
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
    JsonObject jsonPayload = new JsonParser().parse(request.getReader()).getAsJsonObject();
    try {
      String[] requiredFields = {"title"};
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
    // set 204 created status codes
    response.setStatus(CREATED);
  }
}
