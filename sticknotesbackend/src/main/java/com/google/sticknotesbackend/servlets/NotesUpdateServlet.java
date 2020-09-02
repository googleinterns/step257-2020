package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
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


@WebServlet("api/notes-update/")
public class NotesUpdateServlet extends AppAbstractServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson(); 
    
    JsonObject requestBody = new JsonParser().parse(request.getReader()).getAsJsonObject();

    JsonArray requestArray = requestBody.get("notes").getAsJsonArray();

    Type queryListType = new TypeToken<List<UpdateQueryData>>(){}.getType();

    System.out.println(requestArray.toString());

    List<UpdateQueryData> notesQueryArray = gson.fromJson(requestArray.toString(), queryListType); 

    List<Note> notesToReturn = notesQueryArray.stream().filter((query) -> query.wasUpdated()).map((query)->ofy().load().type(Note.class).id(query.id).now()).collect(Collectors.toList());

    String jsonResponse = gson.toJson(notesToReturn);

    System.out.println(jsonResponse);
  }
}
