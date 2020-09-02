package com.google.sticknotesbackend;

import com.google.cloud.datastore.DatastoreOptions;
import com.google.sticknotesbackend.models.Note;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.UserBoardRole;
import com.google.sticknotesbackend.models.Whiteboard;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class NotesboardContextListener implements ServletContextListener {

  /**
   * Initializes Objectify. Registers app models.
   */
  @Override
  public void contextInitialized(ServletContextEvent sce) {
    // for local development we need another config of objectify, to run locally
    // there must be variable RUNMODE=local set
    if (System.getenv("RUNMODE").equals("local")) {
      ObjectifyService.init(new ObjectifyFactory(
          DatastoreOptions.newBuilder().setHost("http://localhost:8484").setProjectId("dummy").build().getService()));
    } else {
      ObjectifyService.init();
    }
    ObjectifyService.register(Whiteboard.class);
    ObjectifyService.register(User.class);
    ObjectifyService.register(Note.class);
    ObjectifyService.register(UserBoardRole.class);
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    // TODO Auto-generated method stub
  }
}
