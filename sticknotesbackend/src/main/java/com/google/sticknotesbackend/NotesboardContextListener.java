package com.google.sticknotesbackend;

import com.google.sticknotesbackend.models.Whiteboard;
import com.googlecode.objectify.ObjectifyService;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class NotesboardContextListener implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    ObjectifyService.init();
    ObjectifyService.register(Whiteboard.class);
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    // TODO Auto-generated method stub
  }
}
