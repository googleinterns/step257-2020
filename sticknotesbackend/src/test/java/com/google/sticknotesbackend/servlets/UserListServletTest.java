package com.google.sticknotesbackend.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.mockito.Mockito.when;
import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.JsonObject;
import com.google.sticknotesbackend.enums.Role;
import com.google.sticknotesbackend.models.User;
import com.google.sticknotesbackend.models.UserBoardRole;
import com.google.sticknotesbackend.models.Whiteboard;
import java.io.BufferedReader;
import com.googlecode.objectify.cache.AsyncCacheFilter;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UserListServletTest extends NotesboardTestBase {

  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private Long boardId1;
  private Long boardId2;
  private Long boardId3;

  private UserListServlet userListServlet;

  @BeforeClass
  public static void setUpBeforeClass() {

    NotesboardTestBase.initializeObjectify();
  }

  @Before
  public void setUp() throws Exception {
    super.setUp();

    try {
      datastoreHelper.reset();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // filling datastore with board and few users
    User user1 = new User("key1", "user1", "user1@google.com");
    User user2 = new User("key2", "user2", "user2@google.com");
    User user3 = new User("key3", "user3", "user3@google.com");
    User user4 = new User("key4", "user4", "user4@google.com");

    Whiteboard board1 = new Whiteboard("title1");
    Whiteboard board2 = new Whiteboard("title2");
    Whiteboard board3 = new Whiteboard("title3");

    ofy().save().entity(user1).now();
    ofy().save().entity(user2).now();
    ofy().save().entity(user3).now();
    ofy().save().entity(user4).now();
    ofy().save().entity(board1).now();
    ofy().save().entity(board2).now();
    ofy().save().entity(board3).now();

    boardId1 = board1.id;
    boardId2 = board2.id;
    boardId3 = board3.id;

    UserBoardRole userBoardRole1 = new UserBoardRole(Role.ADMIN, board1, user1);
    UserBoardRole userBoardRole2 = new UserBoardRole(Role.ADMIN, board1, user2);
    UserBoardRole userBoardRole3 = new UserBoardRole(Role.USER, board1, user3);
    UserBoardRole userBoardRole4 = new UserBoardRole(Role.USER, board1, user4);

    UserBoardRole userBoardRole5 = new UserBoardRole(Role.USER, board2, user3);
    UserBoardRole userBoardRole6 = new UserBoardRole(Role.USER, board2, user4);

    ofy().save().entity(userBoardRole1).now();
    ofy().save().entity(userBoardRole2).now();
    ofy().save().entity(userBoardRole3).now();
    ofy().save().entity(userBoardRole4).now();
    ofy().save().entity(userBoardRole5).now();
    ofy().save().entity(userBoardRole6).now();

    userListServlet = new UserListServlet();

    when(mockRequest.getContentType()).thenReturn("application/json");
    // Set up a fake HTTP response
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
  }

  @After
  public void tearDown() {
    AsyncCacheFilter.complete();
    this.session.close();
    this.helper.tearDown();
  }

  @Test
  public void testBoard1Key() throws IOException {

    when(mockRequest.getParameter("id")).thenReturn(boardId1.toString());

    userListServlet.doGet(mockRequest, mockResponse);

    String expectedResponseJsonString = "["
        + "{\"boardId\":1,\"role\":\"admin\",\"user\":{\"key\":\"key1\",\"nickname\":\"user1\",\"email\":\"user1@google.com\"}},"
        + "{\"boardId\":1,\"role\":\"admin\",\"user\":{\"key\":\"key2\",\"nickname\":\"user2\",\"email\":\"user2@google.com\"}},"
        + "{\"boardId\":1,\"role\":\"user\",\"user\":{\"key\":\"key3\",\"nickname\":\"user3\",\"email\":\"user3@google.com\"}},"
        + "{\"boardId\":1,\"role\":\"user\",\"user\":{\"key\":\"key4\",\"nickname\":\"user4\",\"email\":\"user4@google.com\"}}"
        + "]";

    // veryfing response
    verify(mockResponse).setContentType("application/json");
    verify(mockResponse).setStatus(OK);

    assertThat(responseWriter.toString().equals(expectedResponseJsonString));
  }

  @Test
  public void testBoard2Key() throws IOException {

    when(mockRequest.getParameter("id")).thenReturn(boardId2.toString());

    userListServlet.doGet(mockRequest, mockResponse);
    String expectedResponseJsonString = "["
        + "{\"boardId\":2,\"role\":\"user\",\"user\":{\"key\":\"key3\",\"nickname\":\"user3\",\"email\":\"user3@google.com\"}},"
        + "{\"boardId\":2,\"role\":\"user\",\"user\":{\"key\":\"key4\",\"nickname\":\"user4\",\"email\":\"user4@google.com\"}}"
        + "]";
    // veryfing status
    verify(mockResponse).setContentType("application/json");
    verify(mockResponse).setStatus(OK);
    assertThat(responseWriter.toString().equals(expectedResponseJsonString));
  }

  @Test
  public void testNotExistingBoard() throws IOException {
    Long boardId = (long) 1;
    while (boardId == boardId1 || boardId == boardId2 || boardId == boardId3)
      boardId += 1;

    when(mockRequest.getParameter("id")).thenReturn(boardId.toString());

    userListServlet.doGet(mockRequest, mockResponse);

    // veryfing status
    verify(mockResponse).sendError(BAD_REQUEST);
  }

  @Test
  public void testBoardExistsButNoUsers() throws IOException {
    when(mockRequest.getParameter("id")).thenReturn(boardId3.toString());

    userListServlet.doGet(mockRequest, mockResponse);

    String expectedResponseJsonString = "[]";

    // veryfing status
    verify(mockResponse).setContentType("application/json");
    verify(mockResponse).setStatus(OK);
    assertThat(responseWriter.toString().equals(expectedResponseJsonString));
  }

  /**
   * export interface UserBoardRole {
      user: User;
      boardId: string;
      role: UserRole;
    }   
   */

  // @Test
  // public void testAddUserToBoard() throws IOException{
  //   JsonObject jsonObject = new JsonObject();
  //   jsonObject.addProperty("userId", "key6");
  //   jsonObject.addProperty("role", "admin");
  //   jsonObject.addProperty("boardId", boardId1.toString());

  //   when(mockRequest.getParameter("id")).thenReturn(boardId1.toString());
  //   when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));

  //   userListServlet.doPost(mockRequest, mockResponse);
  // }
}
