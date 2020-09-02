package com.google.sticknotesbackend.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.sticknotesbackend.models.Note;
import com.google.sticknotesbackend.models.UserBoardRole;
import com.google.sticknotesbackend.serializers.NoteSerializer;
import com.google.sticknotesbackend.serializers.UserBoardRoleSerializer;

public abstract class NoteAbstractServlet extends AppAbstractServlet {
  /**
   * Generates a Gson object that uses custom NoteSerializer when serializing Note
   * objects
   */
  protected Gson getNoteGsonParser() {
    GsonBuilder gson = new GsonBuilder();
    gson.registerTypeAdapter(Note.class, new NoteSerializer());
    Gson parser = gson.create();
    return parser;
  }
}
