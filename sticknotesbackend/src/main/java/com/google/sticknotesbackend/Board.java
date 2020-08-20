package com.google.sticknotesbackend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
/**
 * Objectify dataclass for the board
 */
@Entity
public class Board {
  @Id Long id;
  String creationDate;
  String creator;
  String title;
  int rows;
  int cols;

  public Board() {}

  public Board(String title) {
    this.title = title;
  }
}
