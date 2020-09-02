package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.sticknotesbackend.AuthChecker;
import com.google.sticknotesbackend.enums.Permission;
import com.google.sticknotesbackend.exceptions.PayloadValidationException;
import com.google.sticknotesbackend.models.Whiteboard;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Provides endpoint for retrieving board updates
 */
@WebServlet("api/board-updates/")
public class BoardUpdatesServlet extends BoardAbstractServlet {
  /**
   * Returns updated board object if board was updated and client doesn't have the newest version of the board.
   * If client has the most up-to-date version, returns empty JSON object {}
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    JsonObject jsonObject = JsonParser.parseReader(request.getReader()).getAsJsonObject();
    // validate payload
    try {
      String[] requiredFields = {"id", "lastUpdated"};
      validateRequestData(jsonObject, response, requiredFields);
    } catch (PayloadValidationException ex) {
      badRequest(ex.getMessage(), response);
    }
    // get board id from payload
    Long boardId = jsonObject.get("id").getAsLong();
    // check that user has enough rights to access the board
    Permission perm = AuthChecker.boardAccessPermission(boardId);
    if (perm != Permission.GRANTED) {
      handleBadPermission(perm, response);
      return;
    }
    // get client's last updated value from the datastore
    Long clientsValue = jsonObject.get("lastUpdated").getAsLong();
    // check if board in datastore has newer timestamp than the client's board
    Whiteboard board = ofy().load().type(Whiteboard.class).id(boardId).now();
    if (board.lastUpdated != clientsValue) {
      // send update
      Gson gson = getBoardGsonParser();
      response.getWriter().print(gson.toJson(board));
    } else {
      response.getWriter().print("{}");
    }
  }
}
