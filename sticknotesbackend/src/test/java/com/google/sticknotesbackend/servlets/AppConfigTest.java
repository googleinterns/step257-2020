package com.google.sticknotesbackend.servlets;

import static com.google.common.truth.Truth.assertThat;

import com.google.sticknotesbackend.AppConfig;
import com.google.sticknotesbackend.enums.SettingsEntryKey;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for AppConfig class
 */
public class AppConfigTest extends NotesboardTestBase {

  @Before
  public void setUp() throws Exception {
    // parent logic of setting up objectify
    super.setUp();
  }

  @Test
  public void testRetrieveExistingVariableFromDatastoreWorks() {
    String expectedValue = "notesboard";
    // insert setting value in the datastore
    createSettingsEntry(SettingsEntryKey.PROJECT_ID, expectedValue);
    assertThat(AppConfig.getValue(SettingsEntryKey.PROJECT_ID)).isEqualTo(expectedValue);
  }

  @Test
  public void testRetrieveExistingVariableFromEnvironmentWorks() {
    String expectedValue = "notesboard";
    // create mock environment variable
    HashMap<String, Object> envAttr = new HashMap<String, Object>();
    envAttr.put(SettingsEntryKey.PROJECT_ID.configName, expectedValue);
    helper.setEnvAttributes(envAttr);
    assertThat(AppConfig.getValue(SettingsEntryKey.PROJECT_ID)).isEqualTo(expectedValue);
  }
}
