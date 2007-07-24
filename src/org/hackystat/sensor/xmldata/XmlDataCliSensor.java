package org.hackystat.sensor.xmldata;

import java.util.Arrays;
import java.util.Observable;

/**
 * The entry point into the XmlDataSensor. This class allows command-line
 * arguments to be specified by the user to perform actions based on the options and their parameters.
 * 
 * @author Austen Ito
 * 
 */
public class XmlDataCliSensor {
  /** A summary usage message. */
  static final String[] USAGE_MSG = { "\nUsage:\n " + "[-verbose <true|false> "
      + "[-createRunTime <name>] [-sdt <name>]"
      + " [-nameMap <oldAttribute> <newAttribute>]\n"
      + "  -file <filename> [filename]... or -argList <filename>\n"
      + "\n\nNote: optional arguments are within square brackets. "
      + "Arguments can be used in any order." };
  private static XmlDataCliSensor cliSensor = new XmlDataCliSensor();

  /**
   * Provide the command line interface to the XmlData sensor.
   * 
   * @param args The command line arguments.
   */
  public static void main(String[] args) {
    try {
      XmlDataController controller = new XmlDataController(Arrays.asList(args));
      controller.execute();
    }
    catch (Exception e) {
      System.out.println("A fatal error has occured.  Please contact "
          + "your hackystat administrator.");
    }
  }

  /**
   * Constructs an XmlDataCliSensor object, initialzing sensor properties.
   * 
   */
  public XmlDataCliSensor() {
    // this.sensorProps = new SensorProperties("XmlData-Cli");
    // this.isEnabled = this.sensorProps.isSensorEnabled();
  }

  /**
   * Helper method that prints an error message and exits if it is a fatal
   * error.
   * 
   * @param message the message.
   * @param isFatalError true if the program should quit, false if not.
   */
  private void printError(String message, boolean isFatalError) {
    if (!"".equals(message)) { // used so that a two newlines are not printed.
      System.err.println(message);
    }
    if (isFatalError) {
      this.usage();
      System.exit(1);
    }
  }

  /**
   * Display a usage summary message on System.err and exit.
   */
  protected void usage() {
    for (int i = 0; i < USAGE_MSG.length; i++) {
      System.err.println(USAGE_MSG[i]);
    }
  }

  /**
   * Gets the Hackystat host associated with the XmlData CLI sensor.
   * 
   * @return The Hackystat host associated with the XmlData CLI sensor.
   */
  private String getHackystatHost() {
    // return sensorProps.getHackystatHost();
    return "";
  }

  /**
   * Informs the user's of a change in the system.
   * 
   * @param observable the object that has been changed.
   * @param message the message that describes the change.
   */
  public void update(Observable observable, Object message) {
    if (XmlDataController.isVerbose()) {
      System.out.println(message);
    }
  }
}
