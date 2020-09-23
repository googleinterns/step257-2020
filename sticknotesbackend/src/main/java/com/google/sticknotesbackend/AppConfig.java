package com.google.sticknotesbackend;
import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.sticknotesbackend.models.SettingsEntry;
import com.google.sticknotesbackend.enums.SettingsEntryKey;

/**
 * Class that provides static methods for getting config values, either from environment or from datastore Settings
 * class.
 */
public class AppConfig {
  /**
   * Returns value associated with the given key.
   */
  public static String getValue(SettingsEntryKey key) {
    SettingsEntry settings = ofy().load().type(SettingsEntry.class).filter("key", key).first().now();
    if (settings != null) {
      return settings.value;
    } else {
      // tries to fetch variable from environment
      return System.getenv(key.configName);
    }
  }
}
