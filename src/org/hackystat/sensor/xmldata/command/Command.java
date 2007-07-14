package org.hackystat.sensor.xmldata.command;

import org.hackystat.sensor.xmldata.XmlSensorException;

/**
 * Interface that provides a template for the methods that are need to execute a
 * <code>Command</code>.
 * 
 * @author Austen Ito
 * @version $Id:$
 * 
 */
public interface Command {
  /**
   * Validates the object that the <code>Command</code> uses for information when executing.
   * 
   * @return true if valid, false if not.
   */
  public boolean validate ();

  /**
   * Executes this command.
   * 
   * @param object the object the this command requires.
   * @param parameters the parameters that the object uses.
   * @throws XmlSensorException thrown if there is an error while executing.
   */
  public void execute (Object object, Object parameters) throws XmlSensorException;
}
