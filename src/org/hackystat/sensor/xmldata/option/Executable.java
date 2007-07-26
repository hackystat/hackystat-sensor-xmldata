package org.hackystat.sensor.xmldata.option;

/**
 * The interface implemented by options to provide a common way to perform
 * actions based on an option's parameters.
 * @author aito
 * 
 */
public interface Executable {
  /** Executes this option based on the option name and parameters. */
  public void execute();
}
