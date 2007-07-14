
package org.hackystat.sensor.xmldata;

import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import org.hackystat.core.kernel.admin.SensorProperties;
import org.hackystat.sensor.xmldata.command.CommandEvent;
import org.hackystat.sensor.xmldata.command.CommandListener;
import org.hackystat.sensor.xmldata.option.OptionEvent;
import org.hackystat.sensor.xmldata.option.OptionListener;

/**
 * Allows the XmlDataSensor to invoked from a command-line interface.
 * 
 * @author Austen Ito
 * @version $Id:$
 * 
 */
public class XmlDataCliSensor implements CommandListener, OptionListener, Observer {
  /** SensorProperties instance */
  private SensorProperties sensorProps;
  /** Whether the user has enabled this sensor in their properties file. */
  private boolean isEnabled = false;
  /** A summary usage message. */
  static final String[] USAGE_MSG = { "\nUsage:\n " + "[-verbose <true|false> "
      + "[-createRunTime <name>] [-sdt <name>]" + " [-nameMap <oldAttribute> <newAttribute>]\n"
      + "  -file <filename> [filename]... or -argList <filename>\n"
      + "\n\nNote: optional arguments are within square brackets. "
      + "Arguments can be used in any order." };
  private static XmlDataCliSensor cliSensor = new XmlDataCliSensor();

  /**
   * Provide the command line interface to the XmlData sensor.
   * 
   * @param args The command line arguments.
   */
  public static void main (String[] args) {
    XmlDataController.addCommandListeners(cliSensor);
    XmlDataController.addOptionListeners(cliSensor);
    try {
      if (cliSensor.isEnabled()) {
        XmlDataController controller = new XmlDataController(cliSensor, Arrays.asList(args));
        controller.processCommands();
        if (XmlDataController.isVerbose()) {
          System.out.println("Sensor enabled: " + cliSensor.isEnabled());
        }
        controller.execute();
      }
      else {
        cliSensor.printError("Sensor is disabled.  No data sent.", true);
      }
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
    this.sensorProps = new SensorProperties("XmlData-Cli");
    this.isEnabled = this.sensorProps.isSensorEnabled();
  }

  /**
   * Is triggered when a <code>CommandEvent</code> is fired.
   * 
   * @param e the <code>CommandEvent</code> fired from the execution of a <code>Command</code>.
   */
  public void commandPerformed (CommandEvent e) {
    cliSensor.printError(e.getMessage(), e.isFatal());
  }

  /**
   * Is triggered when an option is set.
   * 
   * @param e the <code>OptionEvent</code> fired from the setting of an <code>Option</code>.
   */
  public void setOptionPerformed (OptionEvent e) {
    if (!e.isSet()) {
      System.err.println(e.getMessage());
    }
  }

  /**
   * Helper method that prints an error message and exits if it is a fatal error.
   * 
   * @param message the message.
   * @param isFatalError true if the program should quit, false if not.
   */
  private void printError (String message, boolean isFatalError) {
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
  protected void usage () {
    for (int i = 0; i < USAGE_MSG.length; i++) {
      System.err.println(USAGE_MSG[i]);
    }
  }

  /**
   * Determines whether the XmlData sensor is enabled.
   * 
   * @return true if the XmlData sensor is enabled, false otherwise.
   */
  private boolean isEnabled () {
    return isEnabled;
  }

  /**
   * Gets the Hackystat host associated with the XmlData CLI sensor.
   * 
   * @return The Hackystat host associated with the XmlData CLI sensor.
   */
  private String getHackystatHost () {
    return sensorProps.getHackystatHost();
  }

  /**
   * Informs the user's of a change in the system.
   * 
   * @param observable the object that has been changed.
   * @param message the message that describes the change.
   */
  public void update (Observable observable, Object message) {
    if (XmlDataController.isVerbose()) {
      System.out.println(message);
    }
  }
}
