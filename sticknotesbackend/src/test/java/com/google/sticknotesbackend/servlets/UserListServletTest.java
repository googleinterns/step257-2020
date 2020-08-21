package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.cloud.NoCredentials;
import com.google.cloud.ServiceOptions;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.sticknotesbackend.enums.Role;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.UserBoardRole;
import com.google.sticknotesbackend.models.Whiteboard;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cache.AsyncCacheFilter;
import com.googlecode.objectify.util.Closeable;
import com.google.cloud.datastore.Datastore;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;

import org.mockito.MockitoAnnotations;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UserListServletTest {
  
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  
  private final LocalDatastoreHelper datastoreHelper = LocalDatastoreHelper.create(8081);

  protected Closeable session;

  private Long boardId;

  private UserListServlet userListServlet;

  @BeforeClass
  public static void setUpBeforeClass() {
    DatastoreOptions options = DatastoreOptions.newBuilder()
            .setProjectId("notesboard")
            .setHost("localhost:8081")
            .setCredentials(NoCredentials.getInstance())
            .setRetrySettings(ServiceOptions.getNoRetrySettings())
            .build();
    Datastore datastore = options.getService();
    // Reset the Factory so that all translators work properly.
    ObjectifyService.init(new ObjectifyFactory(datastore));
    ObjectifyService.register(UserBoardRole.class);
    ObjectifyService.register(Whiteboard.class);
    ObjectifyService.register(User.class);
  }



  @Before
  public void setUp() {
    this.session = ObjectifyService.begin();
    this.helper.setUp();
    try{
      datastoreHelper.reset();
    }catch(IOException e){
      e.printStackTrace();
    }
    //filling datastore with board and few users
    User user1 = new User("key1", "user1", "user1@google.com");
    User user2 = new User("key2", "user2", "user1@google.com");
    User user3 = new User("key3", "user3", "user1@google.com");
    User user4 = new User("key4", "user4", "user1@google.com");

    Whiteboard board = new Whiteboard("title");

    ofy().save().entity(user1).now();
    ofy().save().entity(user2).now();
    ofy().save().entity(user3).now();
    ofy().save().entity(user4).now();
    ofy().save().entity(board).now();

    boardId = board.id;
    System.out.println("board id(set-up): " + board.id);

    UserBoardRole userBoardRole1 = new UserBoardRole(Role.ADMIN, board, user1);
    UserBoardRole userBoardRole2 = new UserBoardRole(Role.ADMIN, board, user2);
    UserBoardRole userBoardRole3 = new UserBoardRole(Role.USER, board, user3);
    UserBoardRole userBoardRole4 = new UserBoardRole(Role.USER, board, user4);

    ofy().save().entity(userBoardRole1).now();
    ofy().save().entity(userBoardRole2).now();
    ofy().save().entity(userBoardRole3).now();
    ofy().save().entity(userBoardRole4).now();

    userListServlet = new UserListServlet();
  }

  @After
  public void tearDown() {
    AsyncCacheFilter.complete();
    this.session.close();
    this.helper.tearDown();
  }

  @Test
  public void testDataInDatastore(){
    Iterable<UserBoardRole> query = ofy().load().type(UserBoardRole.class).iterable();
  }

  @Test
  public void testBoardExists() throws IOException {
    HttpServletRequest testRequest = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse testResponse = Mockito.mock(HttpServletResponse.class);

    StringWriter responseWriter = new StringWriter();
    when(testResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    when(testRequest.getParameter("id")).thenReturn(boardId.toString());

    userListServlet.doGet(testRequest, testResponse);

  }
}
