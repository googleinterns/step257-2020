package com.google.sticknotesbackend.enums;

/**
 * Enum for storing all env variables names used in the app
 */
public enum EnvironmentVariable {
  PROJECT_ID("PROJECT_ID"),
  GCS_BUCKET_NAME("GCS_BUCKET_NAME"),
  RUNMODE("RUNMODE");

  public String name;
  private EnvironmentVariable(String name) {
    this.name = name;
  }
}
