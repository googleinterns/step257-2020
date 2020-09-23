/*
* Copyright 2020 Google LLC
*/
package com.google.sticknotesbackend.enums;

public enum Role {
  USER("user"), ADMIN("admin"), OWNER("owner");

  public final String label;

  private Role(String label) {
    this.label = label;
  }
}
