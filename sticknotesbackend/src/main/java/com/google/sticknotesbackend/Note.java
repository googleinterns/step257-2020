package com.google.sticknotesbackend;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

@Entity
public class Note {
  @Id Long id;
  String content;
  String creationDate;
  int x;
  int y;
  String color;
  String creator;
  @Load Ref<Board> board;

  public Note() {
    this.creator = "googler@google.com";
  }

  public Note(String content, String color, int x, int y) {
    this.creator = "googler@google.com";
    this.content = content;
    this.color = color;
    this.x = x;
    this.y = y;
  }

  public Board getBoard() {
    return board.get();
  }

  public void setBoard(Board board) {
    this.board = Ref.create(board);
  }
}
