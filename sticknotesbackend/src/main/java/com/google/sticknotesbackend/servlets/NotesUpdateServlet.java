package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.sticknotesbackend.models.Note;
import com.google.sticknotesbackend.models.Whiteboard;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("api/notes-update/")
public class NotesUpdateServlet extends AppAbstractServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson(); 
    JsonObject requestBody = JsonParser.parseReader(request.getReader()).getAsJsonObject();
    JsonArray requestArray = requestBody.get("notes").getAsJsonArray();
    String boardId = requestBody.get("boardId").getAsString();
    HashMap<Long, Long> queryMap = new HashMap<>();
    for (JsonElement element: requestArray) {
      Long noteId = Long.parseLong(element.getAsJsonObject().get("id").getAsString());
      Long lastUpdated =  Long.parseLong(element.getAsJsonObject().get("lastUpdated").getAsString());
      queryMap.put(noteId, lastUpdated);
    }
    // load board 
    Whiteboard board = ofy().load().type(Whiteboard.class).id(boardId).now();
    // if any of the note has newer timestamp or not present, add to the answer
    ArrayList<Note> notesToReturn = new ArrayList<>();
    board.notes.forEach(noteRef -> {
      Note note = noteRef.get();
      if (!queryMap.containsKey(note.id)) {
        // note is new, add to the response
        notesToReturn.add(note);
      } else {
        Long clientLastUpdate = queryMap.get(note.id);
        if (clientLastUpdate != note.lastUpdated) {
          notesToReturn.add(note);
        }
      }
    });
    response.getWriter().print(gson.toJson(notesToReturn));
  }
}
