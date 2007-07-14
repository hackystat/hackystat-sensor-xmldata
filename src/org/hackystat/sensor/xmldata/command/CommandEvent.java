
package org.hackystat.sensor.xmldata.command;

/**
 * The event that is fired when a <code>Command</code> is executed.
 * 
 * @author Austen Ito
 * @version $Id:$
 * 
 */
public class CommandEvent {
  /** Message about this event */
  private String message = "";
  /** True if data was successfully sent to the hackystat server, false if not */
  private boolean isDataSent = false;
  /** The total number of entries sent. */
  private int entriesSent = 0;
  /** True if the error is fatal, false if not */
  private boolean isFatal = false;

  /**
   * Contructs this event with a filename and whether or not data was successfully sent to the
   * hackystat server.
   * 
   * @param message the message about this event.
   * @param isDataSent true if data was sent to the server, false if not.
   * @param isFatal true if the event is fatal, false if not.
   */
  public CommandEvent(String message, boolean isDataSent, boolean isFatal) {
    this.message = message;
    this.isDataSent = isDataSent;
    this.isFatal = isFatal;
  }

  /**
   * Constructs this event with the total number of entries sent and whether or not the data on
   * these files was sent.
   * 
   * @param entriesSent the total number of entries sent.
   * @param isDataSent true if all data was sent, false if not.
   */
  public CommandEvent(int entriesSent, boolean isDataSent) {
    this.entriesSent = entriesSent;
    this.isDataSent = isDataSent;
  }

  /**
   * Contructs this event with a filename and whether or not data was successfully sent to the
   * hackystat server. A message about this event is also accepted.
   * 
   * @param message A message about this event.
   * @param entriesSent the total number of entries sent. *
   * @param isDataSent true if data was sent to the server, false if not.
   */
  public CommandEvent(String message, int entriesSent, boolean isDataSent) {
    this.message = message;
    this.entriesSent = entriesSent;
    this.isDataSent = isDataSent;
  }

  /**
   * Returns true if data is sent to the server, false if not.
   * 
   * @return true if data was sent to the server, false if not.
   */
  public boolean isDataSent () {
    return this.isDataSent;
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
   * Returns the total number of entries sent.
   * 
   * @return the total number of entries sent.
   */
  public int getEntriesSent () {
    return this.entriesSent;
  }

  /**
   * Returns true if the error associated with the command is fatal. False if not.
   * 
   * @return true if the error is fatal, false if not.
   */
  public boolean isFatal () {
    return isFatal;
  }

}
