package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.Ref;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.HashSet;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.sticknotesbackend.models.Note;
import com.google.sticknotesbackend.models.UpdateQueryData;
import com.google.sticknotesbackend.models.Whiteboard;
import com.google.sticknotesbackend.serializers.NoteSerializer;

@WebServlet("api/notes-update/")
public class NotesUpdateServlet extends AppAbstractServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    JsonObject requestBody = new JsonParser().parse(request.getReader()).getAsJsonObject();

    if (!requestBody.has("notes") && !requestBody.has("boardId")) {
      badRequest("Request has to contain 'notes' and 'boardId' property", response);
      return;
    }

    String boardIdParam = requestBody.get("boardId").getAsString();
    Long boardId = Long.parseLong(boardIdParam);

    JsonArray requestArray = requestBody.get("notes").getAsJsonArray();
    Type queryListType = new TypeToken<List<UpdateQueryData>>() {
    }.getType();
    List<UpdateQueryData> notesQueryArray = gson.fromJson(requestArray.toString(), queryListType);

    List<Note> notesToReturn = notesQueryArray.stream().filter((query) -> query.wasUpdated())
        .map((query) -> ofy().load().type(Note.class).id(query.id).now()).collect(Collectors.toList());

    HashSet<Long> queriedNotesIdSet = new HashSet<>();
    for (UpdateQueryData query : notesQueryArray) {
      queriedNotesIdSet.add(query.id);
    }
    Whiteboard board = ofy().load().type(Whiteboard.class).id(boardId).now();
    if (board != null) {
      for (Ref<Note> noteRef : board.notes) {
        Note note = noteRef.get();
        if (!queriedNotesIdSet.contains(note.id)) {
          notesToReturn.add(note);
        }
      }
    }

    String jsonResponse = getNoteGsonParser().toJson(notesToReturn);

    response.getWriter().println(jsonResponse);
    response.setStatus(OK);
    return;
  }

  private Gson getNoteGsonParser() {
    GsonBuilder gson = new GsonBuilder();
    gson.registerTypeAdapter(Note.class, new NoteSerializer());
    Gson parser = gson.create();
    return parser;
  }
}
