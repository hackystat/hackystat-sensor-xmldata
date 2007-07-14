
package org.hackystat.sensor.xmldata.option;

/**
 * Interface that defines a method that is required to be a listener of <code>Option</code>s.
 * 
 * @author Austen Ito
 * @version $Id:$
 * 
 */
public interface OptionListener {
  /**
   * Is called when a <code>Option</code> is executed.
   * 
   * @param e the <code>OptionEvent</code> associated with the <code>Option</code>.
   */
  public void setOptionPerformed (OptionEvent e);

}
