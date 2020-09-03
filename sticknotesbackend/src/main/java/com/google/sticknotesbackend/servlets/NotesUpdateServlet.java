package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.Ref;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.sticknotesbackend.exceptions.PayloadValidationException;
import com.google.sticknotesbackend.models.Note;
import com.google.sticknotesbackend.models.UpdateQueryData;
import com.google.sticknotesbackend.models.Whiteboard;
import com.google.sticknotesbackend.serializers.NoteSerializer;

@WebServlet("api/notes-update/")
public class NotesUpdateServlet extends AppAbstractServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    JsonObject requestBody = JsonParser.parseReader(request.getReader()).getAsJsonObject();

    //validating request type
    try {
      String[] requiredFields = { "notes", "boardId" };
      validateRequestData(requestBody, response, requiredFields);
    } catch(PayloadValidationException e) {
      badRequest(e.getMessage(), response);
      return;
    }
    Long boardId = requestBody.get("boardId").getAsLong();

    //obtaining type of List<UpdateQueryData> for conversion from JsonArray to List<UpdateQueryData>
    Type queryListType = new TypeToken<List<UpdateQueryData>>() {}.getType();
    List<UpdateQueryData> notesQueryArray = gson.fromJson(requestBody.get("notes").getAsJsonArray(), queryListType);

    //map to store notes from board
    HashMap<Long, Note> notesMap = new HashMap<>();
    Whiteboard board = ofy().load().type(Whiteboard.class).id(boardId).now();
    if (board != null) {
      for (Ref<Note> noteRef : board.notes) {
        Note note = noteRef.get();
        notesMap.put(note.id, note);
      }
    }

    //remove note from map if it was not updated
    for(UpdateQueryData query : notesQueryArray){
      if(notesMap.containsKey(query.id) && notesMap.get(query.id).lastUpdated.equals(query.lastUpdated)){
        notesMap.remove(query.id);
      }
    }

    //creates list of notes to return
    String jsonResponse = getNoteGsonParser().toJson(notesMap.values());

    response.getWriter().println(jsonResponse);
    response.setStatus(OK);
  }

  private Gson getNoteGsonParser() {
    GsonBuilder gson = new GsonBuilder();
    gson.registerTypeAdapter(Note.class, new NoteSerializer());
    Gson parser = gson.create();
    return parser;
  }
}
