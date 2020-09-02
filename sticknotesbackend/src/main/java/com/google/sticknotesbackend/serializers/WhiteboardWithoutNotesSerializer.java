/**
 * Custom serializer for Whiteboard object
 */
package com.google.sticknotesbackend.serializers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.sticknotesbackend.models.Whiteboard;
import java.lang.reflect.Type;

/**
 * Custom serializer to provide more flexible object serialization
 */
public class WhiteboardWithoutNotesSerializer implements JsonSerializer<Whiteboard> {
  /**
   * Custom Whiteboard serialization, excludes notes array from serialization
   */
  @Override
  public JsonElement serialize(Whiteboard src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject board = new JsonObject();
    // add board fields to the JSON objects
    board.addProperty("id", src.id);
    board.addProperty("creationDate", src.creationDate);
    board.addProperty("lastUpdated", src.lastUpdated);
    board.addProperty("title", src.title);
    board.addProperty("rows", src.rows);
    board.addProperty("cols", src.cols);
    board.addProperty("backgroundImg", src.backgroundImg);
    // Creator is the nested element of the JSON object
    board.add("creator", new Gson().toJsonTree(src.getCreator()));
    return board;
  }
}
