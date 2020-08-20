package com.google.sticknotesbackend.models;

import com.google.sticknotesbackend.enums.Role;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Load;

@Entity
public class UserBoardRole {
  String role;
  @Load Ref<Whiteboard> board;
  @Load Ref<User> user;

  public UserBoardRole(Role role, Whiteboard board, User user) {
    this.board = Ref.create(board);
    this.user = Ref.create(user);
    this.role = role.label;
  }
}
