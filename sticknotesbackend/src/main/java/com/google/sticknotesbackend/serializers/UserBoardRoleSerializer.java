package com.google.sticknotesbackend.serializers;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.sticknotesbackend.models.UserBoardRole;

public class UserBoardRoleSerializer implements JsonSerializer<UserBoardRole> {

  @Override
  public JsonElement serialize(UserBoardRole src, Type typeOfSrc, JsonSerializationContext context) {
    // TODO Auto-generated method stub
    JsonObject userBoardRole = new JsonObject();

    //we need in this object board id, user: id, nickname, email, role
    /**
     * interface on frontend:
     *  export interface UserBoardRole {
     *    user: User;
     *    boardKey: string;
     *    role: UserRole;
     *  }
     */
    userBoardRole.addProperty("boardId", src.getBoard().id);  
    userBoardRole.addProperty("role", src.role);
    userBoardRole.add("user", new Gson().toJsonTree(src.getUser()));  

    return userBoardRole;
  }
  
}
