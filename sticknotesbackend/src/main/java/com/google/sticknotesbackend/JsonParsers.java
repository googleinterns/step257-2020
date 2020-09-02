package com.google.sticknotesbackend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.sticknotesbackend.models.Note;
import com.google.sticknotesbackend.models.Whiteboard;
import com.google.sticknotesbackend.serializers.NoteSerializer;
import com.google.sticknotesbackend.serializers.WhiteboardPreviewSerializer;
import com.google.sticknotesbackend.serializers.WhiteboardSerializer;

public class JsonParsers {
  /**
   * Generates a Gson object that uses custom WhiteboardSerializer when
   * serializing Whiteboard objects.
   */
  public static Gson getBoardGsonParser() {
    GsonBuilder gson = new GsonBuilder();
    gson.registerTypeAdapter(Whiteboard.class, new WhiteboardSerializer());
    Gson parser = gson.create();
    return parser;
  }

  /**
   * Generates a Gson object that uses custom NoteSerializer when serializing Note
   * objects
   */
  public static Gson getNoteGsonParser() {
    GsonBuilder gson = new GsonBuilder();
    gson.registerTypeAdapter(Note.class, new NoteSerializer());
    Gson parser = gson.create();
    return parser;
  }
  /**
   * Generates a Gson object that uses custom WhiteboardPreviewSerializer
   */
  public Gson getBoardPreviewGsonParser() {
    GsonBuilder gson = new GsonBuilder();
    gson.registerTypeAdapter(Whiteboard.class, new WhiteboardPreviewSerializer());
    Gson parser = gson.create();
    return parser;
  }
}
