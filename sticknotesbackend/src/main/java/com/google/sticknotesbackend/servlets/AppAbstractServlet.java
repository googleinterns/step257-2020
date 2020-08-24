package com.google.sticknotesbackend.servlets;

import com.google.gson.JsonObject;
import com.google.sticknotesbackend.exceptions.PayloadValidationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

/**
 * Abstract servlet that provides some features used by all servlets
 */
public abstract class AppAbstractServlet extends HttpServlet {
  protected final int CREATED = 201;
  protected final int BAD_REQUEST = 400;
  protected final int NO_CONTENT = 204;

  protected List<String> requiredFields = new ArrayList<>();

  /**
   * Validates JsonObject fields using the list requiredFields. Each field declared in the required fields
   * must be set in the passed JsonObject. "requiredFields" is filled in the derived servlet, each derived
   * servlet sets its necessary fields
   **/
  protected void validateRequestData(JsonObject payload, HttpServletResponse response)
      throws PayloadValidationException {
    // check note has all required fields set
    for (String fieldName : requiredFields) {
      if (!payload.has(fieldName)) {
        throw new PayloadValidationException(fieldName + " field must be set");
      }
    }
  }

  /**
   * Sends a 400 Bad request response with the given message;
   */
  protected void badRequest(String message, HttpServletResponse response) throws IOException {
    response.getWriter().println(message);
    response.sendError(BAD_REQUEST);
  }
}
