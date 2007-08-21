package org.hackystat.sensor.xmldata.option;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.hackystat.sensor.xmldata.XmlDataController;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests if the migration option parameters can be validated correctly and the
 * version 7 data can be converted to version 8 and sent to the sensorbase.
 * @author aito
 * 
 */
public class TestMigrationOption {

  /** Tests if the migration option parameters contain the correct values. */
  @Test
  public void testIsValid() {
    // Creates the test hackystat directory.
    File file = new File("v7useraccount");
    Assert.assertTrue("Failed to create the test dir.", file.mkdir());

    XmlDataController controller = new XmlDataController();
    // Tests a valid migration parameter count.
    List<String> parameters = new ArrayList<String>();
    parameters.add(new File("").getPath());
    parameters.add("v7useraccount");
    parameters.add("v8 user account");
    parameters.add("v8 password");
    Option option = MigrationOption.createOption(controller, parameters);
    Assert.assertTrue("Migration options are invalid.", option.isValid());

    // Cleans up the test hackystat directory.
    Assert.assertTrue("Failed to clean up the test dir.", file.delete());
  }
}
