package com.google.sticknotesbackend.serializers;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.sticknotesbackend.models.Whiteboard;
import java.lang.reflect.Type;

public class WhiteboardPreviewSerializer implements JsonSerializer<Whiteboard> {
  /**
   * Custom Whiteboard serialization, used in board preview, uses only 2 board fields
   */
  @Override
  public JsonElement serialize(Whiteboard src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject board = new JsonObject();
    // add only id and title board fields to the JSON objects
    board.addProperty("id", src.id);
    board.addProperty("title", src.title);
    return board;
  }
}
