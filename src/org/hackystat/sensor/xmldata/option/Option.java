
package org.hackystat.sensor.xmldata.option;

import java.util.List;

import org.hackystat.sensorshell.SensorShell;

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
  public boolean isValid ();
  
  public String getName();
  
  public List<String> getParameters();
  
  public void execute(SensorShell shell);
}
