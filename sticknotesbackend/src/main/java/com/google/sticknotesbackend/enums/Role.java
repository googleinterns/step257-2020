package com.google.sticknotesbackend.enums;

public enum Role {
  USER("user"), ADMIN("admin");

  public final String label;

  private Role(String label) {
    this.label = label;
  }
}
