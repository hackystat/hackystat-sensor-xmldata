package org.hackystat.sensor.xmldata.option;

import java.util.List;

/**
 * The command interface between objects that wrap the options and parameters
 * specified via command-line arguments. Options provide the capability to store
 * the passed options and arguments, validate the parameters, and execute an
 * action over the parameters.
 * @author Austen Ito
 * 
 */
public interface Option {
  /**
   * Returns true if this option's parameters are valid.
   * @return true if all parameters are valid.
   */
  public boolean isValid();

  /**
   * Returns the name of this option.
   * @return the option name.
   */
  public String getName();

  /**
   * Returns a list of parameters used to execute this option.
   * @return the list of parameters.
   */
  public List<String> getParameters();

  /** Executes this option based on the option name and parameters. */
  public void execute();
}
