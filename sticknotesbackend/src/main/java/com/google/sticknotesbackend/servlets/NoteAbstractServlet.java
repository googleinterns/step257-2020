package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.sticknotesbackend.enums.Permission;
import com.google.sticknotesbackend.enums.Role;
import com.google.sticknotesbackend.models.Note;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.UserBoardRole;
import com.google.sticknotesbackend.serializers.NoteSerializer;
import java.util.List;

public abstract class NoteAbstractServlet extends AppAbstractServlet {
  /**
   * Generates a Gson object that uses custom NoteSerializer when serializing Note
   * objects
   */
  protected Gson getNoteGsonParser() {
    GsonBuilder gson = new GsonBuilder();
    gson.registerTypeAdapter(Note.class, new NoteSerializer());
    Gson parser = gson.create();
    return parser;
  }

  /**
   * Checks if current user has enough permissions to modify the note (edit/delete)
   */
  protected Permission noteModifyPermission(Note note) {
    // check that user is authenticated
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      // if the user is author of the note, allow modify
      if (note.getCreator().googleAccId.equals(userService.getCurrentUser().getUserId())) {
        return Permission.GRANTED;
      }
      // if there is a role ADMIN or OWNER with this board and this user, then delete the note
      // get current user
      User user =
          ofy().load().type(User.class).filter("googleAccId", userService.getCurrentUser().getUserId()).first().now();
      // get the list of rules for this board and user
      List<UserBoardRole> boardRoles =
          ofy().load().type(UserBoardRole.class).filter("boardId", note.boardId).filter("user", user).list();
      for (UserBoardRole role : boardRoles) {
        if (role.role.equals(Role.ADMIN)) {
          return Permission.GRANTED;
        }
      }
      return Permission.FORBIDDEN;
    }
    return Permission.NOAUTH;
  }
}
