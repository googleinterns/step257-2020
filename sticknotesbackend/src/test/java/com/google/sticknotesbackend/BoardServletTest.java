package com.google.sticknotesbackend;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.cloud.NoCredentials;
import com.google.cloud.ServiceOptions;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.gson.JsonObject;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for BoardServlet.
 */
@RunWith(JUnit4.class)
public class BoardServletTest {
  // Set up a helper so that the ApiProxy returns a valid environment for local testing.
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig(), new LocalDatastoreServiceTestConfig());
  private Closeable session;

  @Mock private HttpServletRequest mockRequest;
  @Mock private HttpServletResponse mockResponse;
  private StringWriter responseWriter;
  private BoardServlet boardServlet;

  @BeforeClass
  public static void setUpBeforeClass() {
    ObjectifyService.init(new ObjectifyFactory());
    ObjectifyService.register(Board.class);
  }

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    session = ObjectifyService.begin();
    ObjectifyService.register(Board.class);
    // Set up a HTTP request
    when(mockRequest.getContentType()).thenReturn("application/json");
    // Set up a fake HTTP response
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    boardServlet = new BoardServlet();
  }

  @After
  public void tearDown() {
    session.close();
    helper.tearDown();
  }

  @Test
  public void testBoardCreteSuccessWithValidPayload() throws Exception {
    // create mocked request
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("title", "Board title");
    jsonObject.toString();
    when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));
    boardServlet.doPost(mockRequest, mockResponse);
    System.out.println(responseWriter.toString());
    // // We expect our hello world response.
    // assertThat(responseWriter.toString())
    //     .named("HelloAppEngine response")
    //     .contains("Hello App Engine - Standard ");
  }
}
