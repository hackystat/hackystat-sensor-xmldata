
package org.hackystat.sensor.xmldata.option;

/**
 * The event that is fired when a <code>Command</code> is executed.
 * 
 * @author Austen Ito
 * @version $Id:$
 * 
 */
public class OptionEvent {
  /** Message about this event */
  private String message = "";
  /** True if the option was set, false if not. */
  private boolean isSet = false;

  /**
   * Contructs this event with a message about the <code>Option</code>.
   * 
   * @param message the message about the <code>Option</code>.
   * @param isSet true if this option was set, false if not.
   */
  public OptionEvent(String message, boolean isSet) {
    this.message = message;
    this.isSet = isSet;
  }

  /**
   * Returns a message about this event.
   * 
   * @return a message about this event.
   */
  public String getMessage () {
    return this.message;
  }

  /**
   * Returns if this option was set.
   * 
   * @return true if this option was set, false if not.
   */
  public boolean isSet () {
    return this.isSet;
  }
}
