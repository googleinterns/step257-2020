package com.google.sticknotesbackend.models;

import java.io.Serializable;

import com.google.sticknotesbackend.enums.BoardGridLineType;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Entity representing the column(s)/row(s) name of the whiteboard
 */
@Entity
public class BoardGridLine implements Serializable {
  @Id
  public Long id;
  public int rangeStart; // column left border 
  public int rangeEnd; // column right border
  public String title; // column title
  public BoardGridLineType type; // row / column
  public Long boardId; // id of the board to which line is related

  public boolean overlapsWith(BoardGridLine other) {
    // checks if line overlaps with other line
    if (other.type == this.type) {
      return this.rangeStart < other.rangeEnd && other.rangeStart < this.rangeEnd;
    }
    return false;
  }
}
