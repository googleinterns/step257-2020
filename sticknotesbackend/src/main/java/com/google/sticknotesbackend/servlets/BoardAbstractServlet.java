package com.google.sticknotesbackend.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.sticknotesbackend.models.Whiteboard;
import com.google.sticknotesbackend.serializers.WhiteboardSerializer;
import javax.servlet.http.HttpServlet;

/**
 * Provides a common logic for both EditBoardServlet and BoardServlet
 */
public abstract class BoardAbstractServlet extends HttpServlet {
  protected final int CREATED = 201;
  protected final int BAD_REQUEST = 400;

  /**
   * Generates a Gson object that uses custom WhiteboardSerializer when serializing Whiteboard objects.
   */
  public Gson getBoardGsonParser() {
    GsonBuilder gson = new GsonBuilder();
    gson.registerTypeAdapter(Whiteboard.class, new WhiteboardSerializer());
    Gson parser = gson.create();
    return parser;
  }
}
