package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.Ref;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.sticknotesbackend.models.Note;
import com.google.sticknotesbackend.models.UpdateQueryData;
import com.google.sticknotesbackend.models.Whiteboard;


@WebServlet("api/notes-update/")
public class NotesUpdateServlet extends AppAbstractServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String boardIdParam = request.getParameter("id");
    if (boardIdParam == null) {
      badRequest("Error while reading request param.", response);
      return;
    }
    Long boardId = Long.parseLong(boardIdParam);

    Gson gson = new Gson(); 
    JsonObject requestBody = new JsonParser().parse(request.getReader()).getAsJsonObject();

    if(!requestBody.has("notes")){
      badRequest("Request has to contain 'notes' property", response);
      return;
    }

    JsonArray requestArray = requestBody.get("notes").getAsJsonArray();

    Type queryListType = new TypeToken<List<UpdateQueryData>>(){}.getType();

    List<UpdateQueryData> notesQueryArray = gson.fromJson(requestArray.toString(), queryListType); 

    HashSet<Long> idSet = new HashSet<>();
    for(UpdateQueryData query : notesQueryArray){
      idSet.add(query.id);
    }
    
    List<Note> notesToReturn = notesQueryArray.stream().filter((query) -> query.wasUpdated()).map((query)->ofy().load().type(Note.class).id(query.id).now()).collect(Collectors.toList());
    Whiteboard board = ofy().load().type(Whiteboard.class).id(boardId).now();

    for(Ref<Note> noteRef : board.notes){
      Note note = noteRef.get();
      if(!idSet.contains(note.id)){
        notesToReturn.add(note);
      }
    }

    String jsonResponse = gson.toJson(notesToReturn);

    System.out.println(jsonResponse);
  }
}
