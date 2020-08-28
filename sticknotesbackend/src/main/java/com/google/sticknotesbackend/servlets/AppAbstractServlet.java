package com.google.sticknotesbackend.servlets;

import com.google.gson.JsonObject;
import com.google.sticknotesbackend.enums.Permission;
import com.google.sticknotesbackend.exceptions.PayloadValidationException;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

/**
 * Abstract servlet that provides some features used by all servlets
 */
public abstract class AppAbstractServlet extends HttpServlet {
  protected final int OK = 200;
  protected final int CREATED = 201;
  protected final int NO_CONTENT = 204;
  protected final int BAD_REQUEST = 400;
  protected final int UNAUTHORIZED = 401;
  protected final int FORBIDDEN = 403;
  
  

  /**
   * Validates JsonObject fields using the list requiredFields. Each field
   * declared in the required fields must be set in the passed JsonObject.
   * "requiredFields" is filled in the derived servlet, each derived servlet sets
   * its necessary fields
   **/
  protected void validateRequestData(JsonObject payload, HttpServletResponse response, String[] requiredFields)
      throws PayloadValidationException {
    // check note has all required fields set
    for (String fieldName : requiredFields) {
      if (!payload.has(fieldName)) {
        throw new PayloadValidationException(fieldName + " field must be set");
      }
    }
  }

  /**
   * Sends a 400 Bad request response
   */
  protected void badRequest(String message, HttpServletResponse response) throws IOException {
    response.getWriter().println(message);
    response.sendError(BAD_REQUEST);
  }

  /**
   * Send a 401 unauthorized response
   */
  protected void unauthorized(HttpServletResponse response) throws IOException {
    response.getWriter().println("User not authenticated.");
    response.sendError(UNAUTHORIZED);
  }

   /**
   * Send a 403 forbidden response
   */
  protected void forbidden(HttpServletResponse response) throws IOException {
    response.getWriter().println("User is not allowed to use this resource.");
    response.sendError(FORBIDDEN);
  }

  protected void handleBadPermission(Permission perm, HttpServletResponse response) throws IOException {
    if (perm.equals(Permission.FORBIDDEN)) {
      notAllowed(response);
    } else if (perm.equals(Permission.NOAUTH)) {
      unauthorized(response);
    }
  }
}
