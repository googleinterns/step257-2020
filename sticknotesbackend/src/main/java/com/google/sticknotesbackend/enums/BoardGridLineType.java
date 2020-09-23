package com.google.sticknotesbackend.enums;

/**
 * Enum for storing the type of board line (column/row)
 */
public enum BoardGridLineType {
  ROW("row"), COLUMN("column");

  public final String type;

  private BoardGridLineType(String type) {
    this.type = type;
  }
}
