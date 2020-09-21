package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.sticknotesbackend.JsonParsers;
import com.google.sticknotesbackend.enums.Permission;
import com.google.sticknotesbackend.exceptions.PayloadValidationException;
import com.google.sticknotesbackend.models.Note;
import com.google.sticknotesbackend.models.UpdateQueryData;
import com.google.sticknotesbackend.models.Whiteboard;
import com.googlecode.objectify.Ref;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import com.google.sticknotesbackend.AuthChecker;
import com.google.sticknotesbackend.FastStorage;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet is a backend of note live update feature. Client is sending list
 * of queries that are used to check if the note is up to date so request
 * contains following body:
 * "notes":[{"id":231,"lastUpdated":32141424334},{"id":232,"lastUpdated":32141424322},...]
 * response contains list of notes that have been recognized as updated or are
 * new in the board
 */

@WebServlet("api/notes-updates/")
public class NotesUpdateServlet extends AppAbstractServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    JsonObject requestBody = JsonParser.parseReader(request.getReader()).getAsJsonObject();

    // validating request type
    try {
      String[] requiredFields = { "notes", "boardId" };
      validateRequestData(requestBody, response, requiredFields);
    } catch (PayloadValidationException e) {
      badRequest(e.getMessage(), response);
      return;
    }
    Long boardId = requestBody.get("boardId").getAsLong();

    // check that user has enough rights to pull notes update
    Permission perm = AuthChecker.boardAccessPermission(boardId);
    if (perm != Permission.GRANTED) {
      handleBadPermission(perm, response);
      return;
    }
    // obtaining type of List<UpdateQueryData> for conversion from JsonArray to
    // List<UpdateQueryData>
    Type queryListType = new TypeToken<List<UpdateQueryData>>() {
    }.getType();
    List<UpdateQueryData> notesQueryArray = gson.fromJson(requestBody.get("notes").getAsJsonArray(), queryListType);

    // map to store notes from board
    HashMap<Long, Note> notesMap = new HashMap<>();
    Whiteboard board = ofy().load().type(Whiteboard.class).id(boardId).now();
    if (board != null) {
      for (Ref<Note> noteRef : board.notes) {
        Note note = FastStorage.getNote(noteRef.key().getId());
        notesMap.put(note.id, note);
      }
    }

    /*
     * Except information about the updated notes we need to also return information
     * about removed notes. Note is determined as removed from board if it exists in
     * the list from client but doesn't exist in the set of notes of board.
     */
    JsonArray removedNotes = new JsonArray();
    for (UpdateQueryData query : notesQueryArray) {
      if (!notesMap.containsKey(query.id)) {
        removedNotes.add(query.id);
      }
      // remove note from map if it was not updated
      else if (notesMap.get(query.id).lastUpdated != null
          && notesMap.get(query.id).lastUpdated.equals(query.lastUpdated)) {
        notesMap.remove(query.id);
      }
    }

    JsonObject responseBody = new JsonObject();
    responseBody.add("removedNotes", removedNotes);
    responseBody.add("updatedNotes", JsonParsers.getNoteGsonParser().toJsonTree(notesMap.values()));
    String jsonResponse = responseBody.toString();

    response.setContentType("application/json");
    response.getWriter().println(jsonResponse);
    response.setStatus(OK);
  }
}
