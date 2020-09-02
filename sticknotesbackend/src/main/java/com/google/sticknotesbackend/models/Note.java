package com.google.sticknotesbackend.models;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;
import java.sql.Timestamp;

@Entity
public class Note {
  public @Id Long id;
  public String content;
  public String image;
  public Long creationDate; // timestamp here
  public Long lastUpdated;
  public int x;
  public int y;
  public String color;
  @Load Ref<User> creator;
  public Long boardId; // the id of the parent board

  public Note(User user, String content, String color, int x, int y) {
    this.creator = Ref.create(user);
    this.content = content;
    this.color = color;
    this.x = x;
    this.y = y;
  }
  
  public Note() {
    this.x = -1;
    this.y = -1;

    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    creationDate = timestamp.getTime();
    lastUpdated = timestamp.getTime();
  }

  public void setCreator(User user) {
    this.creator = Ref.create(user);
  }

  public User getCreator() {
    return this.creator.get();
  }
  // public Whiteboard getBoard() {
  //   return board.get();
  // }

  // public void setBoard(Whiteboard board) {
  //   this.board = Ref.create(board);
  // }
}
