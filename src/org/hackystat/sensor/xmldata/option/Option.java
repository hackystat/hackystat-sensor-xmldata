
package org.hackystat.sensor.xmldata.option;

import org.hackystat.sensor.xmldata.XmlDataOutputParser;
import org.hackystat.sensor.xmldata.XmlSensorException;

/**
 * Interface that provides a template for the methods that are need to set an
 * <code>Option</code>.
 * 
 * @author Austen Ito
 * @version $Id:$
 * 
 */
public interface Option {
  /**
   * Validates the object that the <code>Option</code> requires.
   * 
   * @return true if valid, false if not.
   */
  public boolean validate ();

  /**
   * Sets this option.
   * 
   * @param parser the <code>XmlDataOutputParser</code> that processes the xml output files.
   * @param object the object that contains information that this class needs.
   * @throws XmlSensorException thrown if there is an error while executing.
   */
  public void set (XmlDataOutputParser parser, Object object) throws XmlSensorException;
}
