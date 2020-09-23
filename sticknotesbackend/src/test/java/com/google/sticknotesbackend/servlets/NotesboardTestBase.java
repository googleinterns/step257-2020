/**
 * Copyright 2020 Google LLC
 */
package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.cloud.NoCredentials;
import com.google.cloud.ServiceOptions;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.sticknotesbackend.enums.BoardGridLineType;
import com.google.sticknotesbackend.enums.Role;
import com.google.sticknotesbackend.enums.SettingsEntryKey;
import com.google.sticknotesbackend.models.BoardGridLine;
import com.google.sticknotesbackend.models.Note;
import com.google.sticknotesbackend.models.SettingsEntry;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.UserBoardRole;
import com.google.sticknotesbackend.models.Whiteboard;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.BeforeClass;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Abstract base class for test classes. Initializes Objectify and local
 * datastore. Provides a few method to build mocked app models
 */
public abstract class NotesboardTestBase {
  protected final int OK = 200;
  protected final int CREATED = 201;
  protected final int NO_CONTENT = 204;
  protected final int BAD_REQUEST = 400;
  protected final int UNAUTHORIZED = 401;
  protected final int FORBIDDEN = 403;

  // Set up a helper so that the ApiProxy returns a valid environment for local
  // testing.
  protected final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(),
      new LocalUserServiceTestConfig(), new LocalMemcacheServiceTestConfig());

  protected Closeable session;

  @Mock
  protected HttpServletRequest mockRequest;
  @Mock
  protected HttpServletResponse mockResponse;
  protected StringWriter responseWriter;

  /**
   * Creates a local datastore, local Objectify service
   */
  @BeforeClass
  public static void initializeObjectify() {
    // necessary setup to make Obejctify work
    DatastoreOptions options = DatastoreOptions.newBuilder().setProjectId("dummy").setHost("localhost:8484")
        .setCredentials(NoCredentials.getInstance()).setRetrySettings(ServiceOptions.getNoRetrySettings()).build();
    Datastore datastore = options.getService();
    ObjectifyService.init(new ObjectifyFactory(datastore));
    ObjectifyService.register(Whiteboard.class);
    ObjectifyService.register(Note.class);
    ObjectifyService.register(User.class);
    ObjectifyService.register(UserBoardRole.class);
    ObjectifyService.register(BoardGridLine.class);
    ObjectifyService.register(SettingsEntry.class);
  }

  /**
   * Initializes Mockito mocks, sets up datastore
   */
  protected void clearDatastore() {
    List<Key<Whiteboard>> keysBoard = ofy().load().type(Whiteboard.class).keys().list();
    ofy().delete().keys(keysBoard).now();
    List<Key<User>> keysUser = ofy().load().type(User.class).keys().list();
    ofy().delete().keys(keysUser).now();
    List<Key<UserBoardRole>> keysBoardRole = ofy().load().type(UserBoardRole.class).keys().list();
    ofy().delete().keys(keysBoardRole).now();
  }

  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    session = ObjectifyService.begin();
  }

  @After
  public void tearDown() {
    session.close();
    helper.tearDown();
  }

  /**
   * Creates a board and stores it in the datastore
   */
  protected Whiteboard createBoard() {
    String uuid = UUID.randomUUID().toString();
    Whiteboard board = new Whiteboard("board title " + uuid);
    board.creationDate = System.currentTimeMillis();
    board.rows = 10;
    board.cols = 10;
    board.id = ofy().save().entity(board).now().getId();
    return board;
  }

  /**
   * Creates a note and stores it in the datastore
   */
  protected Note createNote() {
    String uuid = UUID.randomUUID().toString();
    Note note = new Note();
    note.content = uuid + " note content";
    note.x = 1;
    note.y = 1;
    note.color = "#000000";
    note.id = ofy().save().entity(note).now().getId();
    return note;
  }

  /**
   * Creates a note with creator and lastUpdated, creationDates
   */
  protected Note createNoteWithCreatorAndDates() {
    Note note = createNote();
    note.setCreator(createUser());
    note.lastUpdated = System.currentTimeMillis();
    note.creationDate = System.currentTimeMillis();
    ofy().save().entity(note).now();
    return note;
  }

  /**
   * Creates a mock user and stores the user in the datastore
   */
  private User createUser() {
    // creating mock user with random email and nickname to avoid data duplication
    String uuid = UUID.randomUUID().toString();
    String userEmail = uuid + "@google.com";
    String userNickname = uuid + "-nickname";
    User user = new User(userEmail, userNickname);
    user.googleAccId = uuid;
    user.id = ofy().save().entity(user).now().getId();
    return user;
  }

  /**
   * Creates a mock user and stores the user in the datastore, than waits until
   * user is available on indexes
   */
  protected User createUserSafe() {
    // creating mock user with random email and nickname to avoid data duplication
    User user = createUser();
    while (ofy().load().type(User.class).filter("googleAccId", user.googleAccId).first().now() == null)
      ;
    while (ofy().load().type(User.class).filter("email", user.email).first().now() == null)
      ;
    return user;
  }

  /**
   * Creates a mock userboardrole and stores in the datastore
   */
  protected UserBoardRole createRole(Whiteboard board, User user, Role role) {
    UserBoardRole userBoardRole = new UserBoardRole(role, board, user);
    // save the role
    userBoardRole.id = ofy().save().entity(userBoardRole).now().getId();
    return userBoardRole;
  }

  /**
   * Creates a mock boardgridline and stores it in the datastore
   */
  protected BoardGridLine createBoardGridLine(Long boardId) {
    BoardGridLine line = new BoardGridLine();
    line.type = BoardGridLineType.COLUMN;
    line.boardId = boardId;
    line.rangeStart = 0;
    line.rangeEnd = 2;
    line.title = "title";
    line.id = ofy().save().entity(line).now().getId();
    return line;
  }

  protected SettingsEntry createSettingsEntry(SettingsEntryKey key, String value) {
    SettingsEntry entry = new SettingsEntry();
    entry.key = key;
    entry.value = value;
    entry.id = ofy().save().entity(entry).now().getId();
    return entry;
  }
  /**
   * Logs in given user for a test User object passed here must have googleAccId
   * property set
   */
  protected void logIn(User user) {
    // log the user in
    helper.setEnvIsLoggedIn(true);
    helper.setEnvEmail(user.email);
    helper.setEnvAuthDomain("google.com");
    HashMap<String, Object> envAttr = new HashMap<String, Object>();
    envAttr.put("com.google.appengine.api.users.UserService.user_id_key", user.googleAccId);
    helper.setEnvAttributes(envAttr);
    helper.setUp();
  }
}
