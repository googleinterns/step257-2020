package com.google.sticknotesbackend.enums;

/**
 * Enum of possible keys for app config. If you want to add some config variable to the app, add it's name here firstly
 */
public enum SettingsEntryKey {
  GCS_BUCKET_NAME("GCS_BUCKET_NAME"), // name of the gcs bucket
  PROJECT_ID("PROJECT_ID"); // gc project id

  public final String configName;

  private SettingsEntryKey(String configName) {
    this.configName = configName;
  }
}
