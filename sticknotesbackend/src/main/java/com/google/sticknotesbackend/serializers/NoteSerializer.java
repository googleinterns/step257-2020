/*
* Copyright 2020 Google LLC
*/
package com.google.sticknotesbackend.serializers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.sticknotesbackend.models.Note;
import java.lang.reflect.Type;

/**
 * Custom serializer to provide more flexible object serialization, for example to use method's results as
 * serializer fields
 */
public class NoteSerializer implements JsonSerializer<Note> {

  /**
   * Custom Note serialization
   */
  @Override
  public JsonElement serialize(Note src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject note = new JsonObject();
    // add note fields to JSON object
    note.addProperty("id", src.id);
    note.addProperty("content", src.content);
    note.addProperty("color", src.color);
    note.addProperty("image", src.image);
    note.addProperty("x", src.x);
    note.addProperty("y", src.y);
    note.addProperty("creationDate", src.creationDate);
    note.addProperty("lastUpdated", src.lastUpdated);
    note.add("creator", new Gson().toJsonTree(src.getCreator()));
    return note;
  }
}
