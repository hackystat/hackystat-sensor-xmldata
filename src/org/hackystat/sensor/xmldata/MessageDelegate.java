package org.hackystat.sensor.xmldata;

/**
 * The class which is delegated to by options and the controller when displaying
 * informative messages to the user. This class wraps the way information is
 * displayed to allow the extension of messaging if the mutliple views are added
 * to this sensor, which is currently command-line only.
 * @author aito
 * 
 */
public class MessageDelegate {
  /** The controller used to get user specific information such as verbosity. */
  private XmlDataController controller = null;

  /**
   * Constructs this delegate class with the specified controller.
   * @param controller the specified controller.
   */
  public MessageDelegate(XmlDataController controller) {
    this.controller = controller;
  }

  /**
   * Displays the specified message. The same message is displayed even if the
   * verbose option is enabled.
   * @param message the specified message to display.
   */
  public void displayMessage(String message) {
    System.out.println(message);
  }

  /**
   * Displays the specified message is verbose mode is disabled or the verbose
   * message if verbose mode is enabled.
   * @param message the specified message.
   * @param verboseMessage the specified verbose message.
   */
  public void displayMessage(String message, String verboseMessage) {
    if (this.controller.isVerbose()) {
      System.out.println(verboseMessage);
    }
    else {
      System.out.println(message);
    }
  }
}
