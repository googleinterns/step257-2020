package com.google.sticknotesbackend.models;

import com.google.sticknotesbackend.enums.SettingsEntryKey;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Class for storing settings data in key value form
 */
@Entity
public class SettingsEntry {
  public @Id Long id;
  public @Index SettingsEntryKey key;
  public String value;

  public SettingsEntry() {
    // default ctor must be present because of objectify requirement
  }
}
