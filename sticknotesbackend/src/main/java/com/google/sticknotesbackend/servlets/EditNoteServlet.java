package com.google.sticknotesbackend.servlets;
import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.sticknotesbackend.exceptions.PayloadValidationException;
import com.google.sticknotesbackend.models.Note;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for editing the note
 * implements doPost similar to doPost in NoteServlet, but requires id of the note that is edited
 */
@WebServlet("api/edit-note/")
public class EditNoteServlet extends NoteAbstractServlet {
  /**
   * Edits the note with the id sent in the JSON payload
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // convert request payload to a json object and validate it
    JsonObject jsonPayload = new JsonParser().parse(request.getReader()).getAsJsonObject();
    try {
      String[] requiredFields = {"id"};
      validateRequestData(jsonPayload, response, requiredFields);
    } catch (PayloadValidationException ex) {
      // if exception was thrown, send error message to client
      badRequest(ex.getMessage(), response);
      return;
    }
    // create gson parser that uses custom note serializer
    Gson gson = getNoteGsonParser();
    Note editedNote = gson.fromJson(jsonPayload, Note.class);
    // load requested note from the datastore
    Note note = ofy().load().type(Note.class).id(editedNote.id).now();
    if (note == null) {
      response.getWriter().println("Note with given id does not exist");
      response.sendError(BAD_REQUEST);
      return;
    }
    // assign updated fields to the note retrieved from the datastore
    // if coordinate is not set in the request, it defaults to -1
    if (editedNote.x != -1) {
      note.x = editedNote.x;
    }
    if (editedNote.y != -1) {
      note.y = editedNote.y;
    }
    // next set of fields are updated if they are not null
    if (editedNote.color != null) {
      note.color = editedNote.color;
    }
    if (editedNote.content != null) {
      note.content = editedNote.content;
    }
    if (editedNote.image != null) {
      note.image = editedNote.image;
    }
    // save note
    ofy().save().entity(note).now();
    // return updated note in the response
    response.getWriter().print(gson.toJson(note));
  }
}
