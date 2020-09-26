/**
 * Copyright 2020 Google LLC
 */
package com.google.sticknotesbackend.servlets;

import java.io.IOException;
import static com.googlecode.objectify.ObjectifyService.ofy;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sticknotesbackend.AuthChecker;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.sticknotesbackend.enums.Permission;
import com.google.sticknotesbackend.enums.Role;
import com.google.sticknotesbackend.exceptions.PayloadValidationException;
import com.google.sticknotesbackend.models.UserBoardRole;
import com.google.sticknotesbackend.models.Whiteboard;
import com.googlecode.objectify.Key;

/**
 * This servlet is handling requests for editing user role on the board. Request
 * contains roleId, boardId and new role in its body. Response
 */
@WebServlet("api/edit-role/")
public class EditRoleServlet extends AppAbstractServlet {
  /**
   * Edits role with a given roleId and boardId (because of parent relationship this
   * two properties are needed to identify role). Sets role to the value of
   * newRole property.
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // convert request payload to a json object and validate it
    JsonObject jsonPayload = JsonParser.parseReader(request.getReader()).getAsJsonObject();
    try {
      String[] requiredFields = { "roleId", "boardId", "newRole" };
      validateRequestData(jsonPayload, response, requiredFields);
    } catch (PayloadValidationException ex) {
      // if exception was thrown, send error message to client
      badRequest(ex.getMessage(), response);
      return;
    } 

    Long roleId = jsonPayload.get("roleId").getAsLong();
    Long boardId = jsonPayload.get("boardId").getAsLong();
    Role role = null;
    try {
      role = Role.valueOf(jsonPayload.get("newRole").getAsString().toUpperCase());
    } catch (IllegalArgumentException e) {
      badRequest("Role has to be one of: ADMIN, USER, OWNER", response);
      return;
    }

    Key<Whiteboard> boardKey = Key.create(Whiteboard.class, boardId);
    UserBoardRole boardRole = ofy().load().type(UserBoardRole.class).ancestor(boardKey)
        .filterKey(Key.create(boardKey, UserBoardRole.class, roleId)).first().now();

    // to change user role user needs the same permissions as for modifying the board
    Permission perm = AuthChecker.editRolePermission(boardId, boardRole.role);
    if (!perm.equals(Permission.GRANTED)) {
      handleBadPermission(perm, response);
      return;
    }

    boardRole.role = role;
    ofy().save().entity(boardRole).now();

    response.setStatus(OK);
  }
}
