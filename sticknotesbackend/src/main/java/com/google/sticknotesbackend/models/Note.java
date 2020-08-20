package com.google.sticknotesbackend.models;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

@Entity
public class Note {
  @Id Long id;
  String content;
  String image;
  Long creationDate; //timestamp here
  int x;
  int y;
  String color;
  @Load Ref<User> creator;
  @Load Ref<Whiteboard> board;

  public Note(User user) {
    this.creator = Ref.create(user);
  }

  public Note(User user, String content, String color, int x, int y) {
    this.creator = Ref.create(user);
    this.content = content;
    this.color = color;
    this.x = x;
    this.y = y;
  }

  public Whiteboard getBoard() {
    return board.get();
  }

  public void setBoard(Whiteboard board) {
    this.board = Ref.create(board);
  }
}
