package org.hackystat.sensor.xmldata;

import org.hackystat.sensor.xmldata.option.Options;

/**
 * The class which is delegated to by options and the controller when displaying
 * informative messages to the user. This class wraps the way information is
 * displayed to allow the extension of messaging if the mutliple views are added
 * to this sensor, which is currently command-line only.
 * @author aito
 * 
 */
public class MessageDelegate {
  /** The controller which stores this message delegate. */
  private XmlDataController controller = null;

  /**
   * Constructs this delegate class with the specified controller.
   * @param controller the controller that delegates to this class.
   */
  public MessageDelegate(XmlDataController controller) {
    this.controller = controller;
  }

  /**
   * Displays the specified message. The same message is displayed even if the
   * verbose option is enabled.
   * @param message the specified message to display.
   */
  public void fireMessage(String message) {
    System.out.println(message);
  }

  /**
   * Displays the specified message if verbose mode is enabled.
   * @param message the specified message to display.
   */
  public void fireVerboseMessage(String message) {
    if (Boolean.TRUE.equals(this.controller.getOptionObject(Options.VERBOSE))) {
      System.out.println(message);
    }
  }

  /**
   * Displays the specified message is verbose mode is disabled or the verbose
   * message if verbose mode is enabled.
   * @param message the specified message.
   * @param verboseMessage the specified verbose message.
   */
  public void fireMessage(String message, String verboseMessage) {
    if (Boolean.TRUE.equals(this.controller.getOptionObject(Options.VERBOSE))) {
      System.out.println(verboseMessage);
    }
    else {
      System.out.println(message);
    }
  }
}
