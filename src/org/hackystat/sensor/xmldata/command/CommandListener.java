package org.hackystat.sensor.xmldata.command;

/**
 * Interface that defines a method that is required to be a listener of <code>Command</code>s.
 * 
 * @author Austen Ito
 * @version $Id:$
 * 
 */
public interface CommandListener {
  /**
   * Is called when a <code>Command</code> is executed.
   * 
   * @param e the <code>CommandEvent</code> associated with the <code>Command</code>.
   */
  public void commandPerformed (CommandEvent e);

}
