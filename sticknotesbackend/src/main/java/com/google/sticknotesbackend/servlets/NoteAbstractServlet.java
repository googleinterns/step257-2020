package com.google.sticknotesbackend.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.sticknotesbackend.models.Note;
import com.google.sticknotesbackend.serializers.NoteSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

public abstract class NoteAbstractServlet extends HttpServlet {
  protected final int CREATED = 201;
  protected final int BAD_REQUEST = 400;
  protected final int NO_CONTENT = 204;
  protected List<String> requiredFields = new ArrayList<>();

  /**
   * Generates a Gson object that uses custom NoteSerializer when serializing Note objects
   */
  protected Gson getNoteGsonParser() {
    GsonBuilder gson = new GsonBuilder();
    gson.registerTypeAdapter(Note.class, new NoteSerializer());
    Gson parser = gson.create();
    return parser;
  }

  /** 
   * Validates Note fields using the list requiredFields. Each field declared in the required fields
   * must be set in the passed note object. "requiredFields" is filled in the derived servlet, each derived 
   * servlet sets its necessary fields
  **/
  protected void validateRequestData(Note note, HttpServletResponse response) throws IOException {
    // check note has all required fields set
    for (String fieldName : requiredFields) {
      try {
        // use reflection api to get the value of the field, and if it is null send error
        if (Note.class.getField(fieldName).get(note) == null) {
          response.getWriter().println(fieldName + " field must be set");
          response.sendError(BAD_REQUEST);
          return;
        }
      } catch (IllegalAccessException | NoSuchFieldException ex) {
        response.getWriter().println("Error occurred while validating the payload");
        response.sendError(BAD_REQUEST);
        return;
      }
    }
  }
}
