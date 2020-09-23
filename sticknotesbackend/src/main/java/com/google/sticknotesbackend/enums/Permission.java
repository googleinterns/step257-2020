package com.google.sticknotesbackend.enums;

public enum Permission {
  GRANTED("granted"), FORBIDDEN("forbidden"), NOAUTH("noauth");

  public final String label;

  private Permission(String label) {
    this.label = label;
  }
}
