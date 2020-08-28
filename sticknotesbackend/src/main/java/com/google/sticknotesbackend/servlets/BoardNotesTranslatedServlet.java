package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.sticknotesbackend.AuthChecker;
import com.google.sticknotesbackend.enums.Permission;
import com.google.sticknotesbackend.models.Note;
import com.google.sticknotesbackend.models.Whiteboard;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Provides a notes translation feature
 */
@WebServlet("api/board/notes/")
public class BoardNotesTranslatedServlet extends NoteAbstractServlet {
  /**
   * Required URL params are "id" and "lc"
   * Takes notes of the board with id = "id" and translate notes content to the language with language code = "lc"
   * Returns translated array of notes
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // get and validate URL parameters
    String boardIdParam = request.getParameter("id");
    String languageCode = request.getParameter("lc");
    if (boardIdParam == null || languageCode == null) {
      badRequest("Board id {id} and language code {lc} URL params must be set", response);
      return;
    }
    Long boardId = Long.parseLong(boardIdParam);
    Permission perm = AuthChecker.boardAccessPermission(boardId);
    if (!perm.equals(Permission.GRANTED)) {
      handleBadPermission(perm, response);
      return;
    }
    // get the board which notes have to be translated
    Whiteboard board = ofy().load().type(Whiteboard.class).id(boardId).now();
    // get the list of notes from list of references store in the board
    List<Note> notes = board.notes.stream().map(noteRef -> noteRef.get()).collect(Collectors.toList());
    // list of translated notes to return
    JsonArray translatedNotesJsonArray = new JsonArray();
    Gson gson = getNoteGsonParser();
    Translate translate = TranslateOptions.getDefaultInstance().getService();
    notes.forEach(note -> {
      // translate each note
      Translation translation =
          translate.translate(note.content, Translate.TranslateOption.targetLanguage(languageCode));
      note.content = translation.getTranslatedText();
      // add JsonObject of note to the translatedNotesJsonArray response
      // it uses custom note serializer
      translatedNotesJsonArray.add(gson.toJsonTree(note));
    });
    response.getWriter().print(toString());
  }
}
