
package org.hackystat.sensor.xmldata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import junit.framework.TestCase;

import org.hackystat.core.kernel.admin.ServerProperties;
import org.hackystat.core.kernel.user.User;
import org.hackystat.core.kernel.user.UserManager;
import org.hackystat.core.kernel.util.ExtensionFileFilter;
import org.hackystat.sensor.xmldata.command.CommandEvent;
import org.hackystat.sensor.xmldata.command.CommandListener;
import org.hackystat.sensor.xmldata.option.OptionEvent;
import org.hackystat.sensor.xmldata.option.OptionListener;

/**
 * Tests the Ant XmlDataSensor.
 * 
 * @author Aaron A. Kagawa
 * @author Austen Ito
 * @version $Id$
 */
public class TestDataXmlSensor extends TestCase implements CommandListener, OptionListener,
    Observer {
  /** The test user */
  private User testUser = UserManager.getInstance().getTestUser();
  /** ServerProperties instance */
  private ServerProperties properties = ServerProperties.getInstance();
  /** Set to true if the test case running checks to see if a fatal error occurs. */
  private boolean isTestingFail = false;
  /**
   * The number of entries sent to hackystat. This number does not reflect the actual number
   * recieved by the server, but it reflects the number of entries sent by the sensor.
   */
  private int correctEntriesSent = 0;

  /** Sets up this test class. */
  public void setUp () {
    this.correctEntriesSent = 0;
    XmlDataController.addCommandListeners(this);
    XmlDataController.addOptionListeners(this);
  }

  /**
   * Tests sending a normal file, which is a file with all of the correct attributes.
   * 
   * @throws IOException thrown if a file cannot be accessed.
   */
  public void testNormalFile () throws IOException {
    this.correctEntriesSent = 2;
    File testdataFilesDirectory = new File(this.properties.getUserDir(testUser),
                                           "xmldatatestfiles");

    if (!testdataFilesDirectory.isDirectory()) {
      fail("Cannot find xml sensor data senor test files.");
    }
    File[] files = testdataFilesDirectory.listFiles();
    ExtensionFileFilter filter = new ExtensionFileFilter(".xml");
    for (int j = 0; j < files.length; j++) {
      if (filter.accept((File) files[j])) {
        String fileName = ((File) files[j]).getName();
        if ("Normal_filemetric.xml".equals(fileName)) {
          String[] args = { "-file", files[j].getPath() };
          XmlDataController controller = new XmlDataController(this, Arrays.asList(args));
          controller.processCommands();
          controller.execute();
        }
      }
    }
  }

  /**
   * Tests if the only file to send data from is invalid.
   * 
   * @throws IOException thrown if the file cannot be accessed.
   */
  public void testIncorrectFile () throws IOException {
    this.correctEntriesSent = 0;
    String[] args = { "-file", "foo.xml" };
    XmlDataController controller = new XmlDataController(this, Arrays.asList(args));
    controller.processCommands();
    controller.execute();
  }

  /**
   * Tests if the entries from multiple valid normal files are sent, while an invalid file is
   * ignored.
   * 
   * @throws IOException thrown if a file cannot be accessed.
   */
  public void testTwoValidOneInvalidFile () throws IOException {
    this.correctEntriesSent = 6;
    File testdataFilesDirectory = new File(this.properties.getUserDir(testUser),
                                           "xmldatatestfiles");

    if (!testdataFilesDirectory.isDirectory()) {
      fail("Cannot find xml sensor data senor test files.");
    }
    // gets normal files, which are files that have all the correct fields.
    File[] files = testdataFilesDirectory.listFiles();
    ExtensionFileFilter filter = new ExtensionFileFilter(".xml");
    List args = new ArrayList();
    args.add("-file");
    for (int j = 0; j < files.length; j++) {
      if (filter.accept((File) files[j])) {
        String fileName = ((File) files[j]).getName();
        if (fileName.startsWith("Normal")) {
          args.add(files[j].getPath());
        }
      }
    }
    args.add("foo.xml");
    // send data
    XmlDataController controller = new XmlDataController(this, args);
    controller.processCommands();
    controller.execute();
  }

  /**
   * Tests the sensor by sending data from a file that requires the sdt name to be set and nameMaps
   * implemented.
   * 
   * @throws IOException thrown if a file cannot be read.
   */
  public void testSendNonStandardFile () throws IOException {
    this.correctEntriesSent = 6;
    File testdataFilesDirectory = new File(this.properties.getUserDir(testUser),
                                           "xmldatatestfiles");

    if (!testdataFilesDirectory.isDirectory()) {
      fail("Cannot find xml sensor data senor test files.");
    }
    List args = new ArrayList();
    args.add("-file");
    // gets normal files, which are files that have all the correct fields.
    File[] files = testdataFilesDirectory.listFiles();
    ExtensionFileFilter filter = new ExtensionFileFilter(".xml");
    for (int j = 0; j < files.length; j++) {
      if (filter.accept((File) files[j])) {
        String fileName = ((File) files[j]).getName();
        if (fileName.equals("evolsdt.xml")) {
          args.add(files[j].getPath());
        }
      }
    }
    String[] nameMaps = { "-sdt", "SampleSdt", "-nameMap", "elapsedTime", "time", "-nameMap",
                         "name", "fileName", "-nameMap", "tool", "toolName" };
    args.addAll(Arrays.asList(nameMaps));
    // send data
    XmlDataController controller = new XmlDataController(this, args);
    controller.processCommands();
    controller.execute();
  }

  /**
   * Tests if the -createRunTime option is set correctly.
   * 
   * @throws IOException thrown if a file cannot be accessed.
   */
  public void testCreateRunTime () throws IOException {
    String[] args = { "-file", "foo.xml", "-createRunTime", "runtime" };
    // send data
    XmlDataController controller = new XmlDataController(this, Arrays.asList(args));
    controller.processCommands();
    controller.execute();
  }

  /**
   * Tests if multiple normal files are sent correctly. A normal file is a file that contains all of
   * the correct attributes needed by the sensor data type.
   * 
   * @throws IOException thrown if a file cannot be accessed.
   */
  public void testSendingMultipleNormalFiles () throws IOException {
    this.correctEntriesSent = 6;
    File testdataFilesDirectory = new File(this.properties.getUserDir(testUser),
                                           "xmldatatestfiles");

    if (!testdataFilesDirectory.isDirectory()) {
      fail("Cannot find xml sensor data senor test files.");
    }
    // gets normal files, which are files that have all the correct fields.
    File[] files = testdataFilesDirectory.listFiles();
    ExtensionFileFilter filter = new ExtensionFileFilter(".xml");
    List args = new ArrayList();
    args.add("-file");
    for (int j = 0; j < files.length; j++) {
      if (filter.accept((File) files[j])) {
        String fileName = ((File) files[j]).getName();
        if (fileName.startsWith("Normal")) {
          args.add(files[j].getPath());
        }
      }
    }
    // send data
    XmlDataController controller = new XmlDataController(this, args);
    controller.processCommands();
    controller.execute();
  }

  /**
   * Tests if the -argList command runs correctly.
   * 
   * @throws IOException thrown if a file cannot be accessed.
   */
  public void testArgListCommand () throws IOException {
    this.correctEntriesSent = 12;
    File testdataFilesDirectory = new File(this.properties.getUserDir(testUser),
                                           "xmldatatestfiles");
    if (!testdataFilesDirectory.isDirectory()) {
      fail("Cannot find xml sensor data senor test files.");
    }
    // gets normal files, which are files that have all the correct fields.
    File[] files = testdataFilesDirectory.listFiles();
    ExtensionFileFilter filter = new ExtensionFileFilter(".txt");
    List args = new ArrayList();
    args.add("-argList");
    for (int j = 0; j < files.length; j++) {
      if (filter.accept((File) files[j])) {
        String fileName = ((File) files[j]).getName();
        if ("argList.txt".equals(fileName)) {
          args.add(files[j].getPath());
        }
      }
    }

    // send data
    XmlDataController controller = new XmlDataController(this, args);
    controller.processCommands();
    controller.execute();
  }

  /**
   * Tests if a fatal error occurs when the -argList and -file command is used.
   * 
   * @throws IOException thrown if a file cannot be accessed.
   */
  public void testArgListAndFileCommand () throws IOException {
    this.isTestingFail = true;
    String[] args = { "-argList", "fooArgs.txt", "-file", "foo.xml" };
    XmlDataController controller = new XmlDataController(this, Arrays.asList(args));
    controller.processCommands();
    controller.execute();
  }

  /**
   * Tests if the -verbose option is set correctly.
   * 
   * @throws IOException thrown if a file cannot be accessed.
   */
  public void testVerbose () throws IOException {
    String[] args = { "-file", "foo.xml", "-verbose", "true" };
    // send data
    XmlDataController controller = new XmlDataController(this, Arrays.asList(args));
    controller.processCommands();
    controller.execute();

    String[] args2 = { "-file", "foo.xml", "-verbose", "yes" };
    // send data
    XmlDataController controller2 = new XmlDataController(this, Arrays.asList(args2));
    controller2.processCommands();
    controller2.execute();

    String[] args3 = { "-file", "foo.xml", "-verbose", "on" };
    // send data
    XmlDataController controller3 = new XmlDataController(this, Arrays.asList(args3));
    controller3.processCommands();
    controller3.execute();

    String[] args4 = { "-file", "foo.xml", "-verbose", "off" };
    // send data
    XmlDataController controller4 = new XmlDataController(this, Arrays.asList(args4));
    controller4.processCommands();
    controller4.execute();

    String[] args5 = { "-file", "foo.xml", "-verbose", "foo" };
    // send data
    XmlDataController controller5 = new XmlDataController(this, Arrays.asList(args5));
    controller5.processCommands();
    controller5.execute();
  }

  /**
   * Triggered if a <code>Command</code> is performed.
   * 
   * @param e the event associated with the command.
   */
  public void commandPerformed (CommandEvent e) {
    if (this.isTestingFail) { // testing incorrect arguments
      assertTrue(e.getMessage(), e.isFatal());
    }
    else if (e.isDataSent()) {
      assertEquals("Invalid amount of entries sent.", this.correctEntriesSent, e.getEntriesSent());
    }
    else if (e.isFatal()) { // data was not set
      fail(e.getMessage());
    }
  }

  /**
   * Triggered if a <code>Option</code> is set.
   * 
   * @param e the event associated with an option being set.
   */
  public void setOptionPerformed (OptionEvent e) {
    if (this.isTestingFail && e.isSet()) {
      fail("The option should not have been set");
    }
    else if (!e.isSet()) {
      fail("The following option should have been set, but was not: " + e.getMessage());
    }
  }

  /**
   * Is called when verbose mode is on. Addtional information is displayed to the user by classes
   * that implement the Observer interface.
   * 
   * @param observable the object that is observable
   * @param message the message describing the change.
   */
  public void update (Observable observable, Object message) {
    if (!XmlDataController.isVerbose()) {
      fail("A message (" + message + ") is printed, but verbose mode is off");
    }
  }
}