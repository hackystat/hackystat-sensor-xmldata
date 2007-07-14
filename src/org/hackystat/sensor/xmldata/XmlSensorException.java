package org.hackystat.sensor.xmldata;

/**
 * Indicates problems with the XmlSensorData sensor.
 *
 * @author Aaron A. Kagawa
 * @version $Id$
 */
public class XmlSensorException extends Exception {

  /** Compiler generated serial version id. */
  private static final long serialVersionUID = -7326982204294076341L;

  /** Constructor when there is no prior exception to chain. */
  public XmlSensorException() {
  }

  /**
   * Constructor when detail message but no prior exception to chain.
   * @param message Print message.
   */
  public XmlSensorException(String message) {
    super(message);
  }

  /**
   * Constructor to produce an exception with a prior exception with a detail message.
   * @param message The detail message.
   * @param exception The causal exception/throwable.
   */
  public XmlSensorException(String message, Throwable exception) {
    super(message, exception);
  }
}