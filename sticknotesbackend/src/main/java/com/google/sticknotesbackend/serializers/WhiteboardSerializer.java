/**
  * Copyright 2020 Google LLC
  *
  * Custom serializer for Whiteboard object
 */
package com.google.sticknotesbackend.serializers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.sticknotesbackend.models.BoardGridLine;
import com.google.sticknotesbackend.models.Note;
import com.google.sticknotesbackend.models.Whiteboard;
import com.googlecode.objectify.Ref;
import java.lang.reflect.Type;

/**
 * Custom serializer to provide more flexible object serialization, for example to use method's results as
 * serializer fields
 */
public class WhiteboardSerializer implements JsonSerializer<Whiteboard> {

  /**
   * Custom Whiteboard serialization, creates an array of notes from notes references that stored in the Whiteboard
   * object
   */
  @Override
  public JsonElement serialize(Whiteboard src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject board = new JsonObject();
    // add board fields to the JSON objects
    board.addProperty("id", src.id);
    board.addProperty("creationDate", src.creationDate);
    board.addProperty("title", src.title);
    board.addProperty("rows", src.rows);
    board.addProperty("cols", src.cols);
    board.addProperty("backgroundImg", src.backgroundImg);
    // Creator is the nested element of the JSON object
    board.add("creator", new Gson().toJsonTree(src.getCreator()));
    // add notes using NoteSerializer
    // create a gson object with registered NoteSerializer
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(Note.class, new NoteSerializer());
    Gson gson = gsonBuilder.create();
    // create gson array
    JsonArray notesArray = new JsonArray();
    for (Ref<Note> noteRef: src.notes) {
      Note note = noteRef.get();
      if (note != null) {
        notesArray.add(gson.toJsonTree(note));
      }
    }
    // add array as "notes" property
    board.add("notes", notesArray);
    // add column/rows names
    if (src.gridLines != null) {
      // create json array for grid lines
      JsonArray linesArr = new JsonArray();
      for (Ref<BoardGridLine> lineRef: src.gridLines) {
        linesArr.add(gson.toJsonTree(lineRef.get()));
      }
      board.add("gridLines", linesArr);
    }
    return board;
  }
}
