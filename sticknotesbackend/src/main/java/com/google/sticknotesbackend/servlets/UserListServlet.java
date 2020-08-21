package com.google.sticknotesbackend.servlets;

import java.io.IOException;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.sticknotesbackend.serializers.UserBoardRoleSerializer;
import com.google.sticknotesbackend.models.UserBoardRole;
import com.google.sticknotesbackend.models.Whiteboard;

@WebServlet("api/board/users")
public class UserListServlet extends HttpServlet {
  protected final int CREATED = 201;
  protected final int BAD_REQUEST = 400;
  protected final int OK = 200;

  // with a given id it returns list of users of the board
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String boardIdParam = request.getParameter("id");
    Long boardId = null;
    try {
      boardId = Long.valueOf(boardIdParam);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    if (boardId != null) {
      Whiteboard board = ofy().load().type(Whiteboard.class).id(boardId).now();
      if (board != null) {
        List<UserBoardRole> boardUsers = ofy().load().type(UserBoardRole.class).filter("board", board).list();
        Gson userBoardRoleParser = getBoardGsonParser();

        String responseJson = userBoardRoleParser.toJson(boardUsers);
        response.setStatus(OK);
        response.setContentType("application/json");
        response.getWriter().println(responseJson);
      } else {
        response.getWriter().println("Board with this id doesn't exist");
        response.sendError(BAD_REQUEST);
        return;
      }
    } else {
      response.getWriter().println("Board id was not provided");
      response.sendError(BAD_REQUEST);
      return;
    }
  }

  public Gson getBoardGsonParser() {
    GsonBuilder gson = new GsonBuilder();
    gson.registerTypeAdapter(UserBoardRole.class, new UserBoardRoleSerializer());
    Gson parser = gson.create();
    return parser;
  }
}
