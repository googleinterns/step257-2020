package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.sticknotesbackend.AuthChecker;
import com.google.sticknotesbackend.FastStorage;
import com.google.sticknotesbackend.JsonParsers;
import com.google.sticknotesbackend.enums.Permission;
import com.google.sticknotesbackend.exceptions.PayloadValidationException;
import com.google.sticknotesbackend.models.Note;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.Whiteboard;
import com.googlecode.objectify.Ref;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that handles Note logic Implements methods: * POST - create note
 */
@WebServlet("api/notes/")
public class NoteServlet extends AppAbstractServlet {
  /**
   * Creates a Note The expected JSON payload is boardId: id of the board content:
   * the content of the note; image: url / base64 (not decided yet); color:
   * string; x: the coordinate y: another coordinate
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // check that user is authenticated
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      unauthorized(response);
      return;
    }
    // convert request payload to a json object and validate it
    JsonObject jsonPayload = JsonParser.parseReader(request.getReader()).getAsJsonObject();
    try {
      String[] requiredFields = { "content", "boardId", "color", "x", "y" };
      validateRequestData(jsonPayload, response, requiredFields);
    } catch (PayloadValidationException ex) {
      // if exception was thrown, send error message to client
      badRequest(ex.getMessage(), response);
      return;
    }
    // create gson parser that uses custom note serializer
    Gson gson = JsonParsers.getNoteGsonParser();
    Note note = gson.fromJson(jsonPayload, Note.class);
    // get the board of the note
    // check that the position of note doesn't clash with the position of other notes
    Whiteboard board = ofy().load().type(Whiteboard.class).id(note.boardId).now();
    for (Ref<Note> noteRef: board.notes) {
      Note existingNote = noteRef.get();
      FastStorage.getNote(noteRef.key().getRaw().getId());
      if (existingNote.x == note.x && existingNote.y == note.y) {
        badRequest("Coordinates already taken", response);
        return;
      }
    }
    // get currently logged in user from the datastore
    User user = ofy().load().type(User.class).filter("googleAccId", userService.getCurrentUser().getUserId()).first()
        .now();
    note.setCreator(user);
    note.creationDate = System.currentTimeMillis();
    note.lastUpdated = System.currentTimeMillis();
    
    // save the note and set id
    FastStorage.updateNote(note);
    
    // note.id = ofy().save().entity(note).now().getId();
    // add reference to the note at this board
    board.notes.add(Ref.create(note));
    ofy().save().entity(board).now();
    // return the note
    response.getWriter().print(gson.toJson(note));
    // set 204 created status code
    response.setStatus(CREATED);
  }

  /**
   * Deletes the note with the given id Id must be passed as a url param "id"
   */
  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // get note id from the request
    String noteIdParam = request.getParameter("id");
    if (noteIdParam != null) {
      Long noteId = Long.parseLong(noteIdParam);
      
      // get note that is going to be deleted
      Note note = FastStorage.getNote(noteId);//ofy().load().type(Note.class).id(noteId).now();
      
      // check if user has enough permissions to modify note
      Permission perm = AuthChecker.noteModifyPermission(note);
      if (!perm.equals(Permission.GRANTED)) {
        handleBadPermission(perm, response);
        return;
      }
      // get note board
      Whiteboard noteBoard = ofy().load().type(Whiteboard.class).id(note.boardId).now();
      // remove note from the list of noteBoard notes
      noteBoard.notes.removeIf(noteRef -> noteRef.get().id == note.id);
      // update noteBoard
      ofy().save().entity(noteBoard).now();
      // delete note from datastore
      
      // ofy().delete().type(Note.class).id(noteId).now();
      FastStorage.removeNote(note);
      
      response.setStatus(NO_CONTENT);
    } else {
      // if note id was not passed as a URL param, return 400 bad request error
      response.getWriter().println("No id set");
      response.sendError(BAD_REQUEST);
    }
  }
}
