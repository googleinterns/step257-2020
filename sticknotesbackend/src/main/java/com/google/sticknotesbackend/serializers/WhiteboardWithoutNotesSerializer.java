/**
 * Copyright 2020 Google LLC
 * 
 * Custom serializer for Whiteboard object
 */
package com.google.sticknotesbackend.serializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.sticknotesbackend.models.BoardGridLine;
import com.google.sticknotesbackend.models.Whiteboard;
import com.googlecode.objectify.Ref;
import com.google.gson.Gson;

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

    if (src.gridLines != null) {
      // create json array for grid lines
      JsonArray linesArr = new JsonArray();
      for (Ref<BoardGridLine> lineRef: src.gridLines) {
        linesArr.add(new Gson().toJsonTree(lineRef.get()));
      }
      board.add("gridLines", linesArr);
    }
    return board;
  }
}
