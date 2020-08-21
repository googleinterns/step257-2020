package com.google.sticknotesbackend.servlets;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.cloud.NoCredentials;
import com.google.cloud.ServiceOptions;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.Whiteboard;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public abstract class NotesboardTestBase {
  protected final int CREATED = 201;
  protected final int BAD_REQUEST = 400;
  // Set up a helper so that the ApiProxy returns a valid environment for local testing.
  protected final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig(), new LocalDatastoreServiceTestConfig());
  protected Closeable session;

  @Mock protected HttpServletRequest mockRequest;
  @Mock protected HttpServletResponse mockResponse;
  protected StringWriter responseWriter;

  public static void initializeObjectify() {
    // necessary setup to make Obejctify work
    DatastoreOptions options = DatastoreOptions.newBuilder()
                                   .setProjectId("dummy")
                                   .setHost("localhost:8484")
                                   .setCredentials(NoCredentials.getInstance())
                                   .setRetrySettings(ServiceOptions.getNoRetrySettings())
                                   .build();
    Datastore datastore = options.getService();
    ObjectifyService.init(new ObjectifyFactory(datastore));
    ObjectifyService.register(Whiteboard.class);
    ObjectifyService.register(User.class);
  }

  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    session = ObjectifyService.begin();
  }

  public void tearDown() {
    session.close();
    helper.tearDown();
  }

  // helper method that constructs a testing object of Whiteboard
  protected Whiteboard getMockBoard() {
    Whiteboard board = new Whiteboard("test");
    board.creationDate = System.currentTimeMillis();
    // create dummy user and set this user as a creator of the board
    board.setCreator(new User("randomid", "googler@google.com", "nickname"));
    board.rows = 4;
    board.cols = 6;
    return board;
  }
}
