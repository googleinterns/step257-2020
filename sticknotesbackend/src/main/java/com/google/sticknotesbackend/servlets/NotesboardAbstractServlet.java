package com.google.sticknotesbackend;

/**
 * Notesboard
 * Abstract custom servlet NotesboardAbstractServlet
 */

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Custom servlet that allows HTTP PATCH method
 */
public abstract class NotesboardAbstractServlet extends HttpServlet {
  // to allow patch method
  public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    if (request.getMethod().equalsIgnoreCase("patch")){
       doPatch(request, response);
    } else {
        super.service(request, response);
    }
  }

  public abstract void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

}
