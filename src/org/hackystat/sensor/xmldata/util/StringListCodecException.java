package org.hackystat.sensor.xmldata.util;

/**
 * Thrown when exceptions occur during string list encoding and decoding. This class implements
 * exception chaining. Use printStackTrace to see all exceptions.
 *
 * @author    Philip Johnson
 * @version   $Id: StringListCodecException.java,v 1.1.1.1 2005/10/20 23:56:44 johnson Exp $
 */
@SuppressWarnings("serial")
public class StringListCodecException extends Exception {

  /**
   * Thrown when exceptions occur during string list encoding and decoding.
   *
   * @param detailMessage      A message describing the problem.
   * @param previousException  A possibly null reference to a prior exception.
   */
  public StringListCodecException(String detailMessage, Throwable previousException) {
    super(detailMessage, previousException);
  }

  /**
   * Thrown when exceptions occur during elapsed time processing.
   *
   * @param detailMessage  A message describing the problem.
   */
  public StringListCodecException(String detailMessage) {
    super(detailMessage, null);
  }


}
