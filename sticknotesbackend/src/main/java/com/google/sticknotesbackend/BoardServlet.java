/**
 * Notesboard
 * Board API servlet
 */
package com.google.sticknotesbackend;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
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
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    

  }

  // edits a board
  @Override
  public void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
  }
}
