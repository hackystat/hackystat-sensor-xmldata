package org.hackystat.sensor.xmldata.option;

import java.util.ArrayList;
import java.util.List;

import org.hackystat.sensor.xmldata.XmlDataController;

/**
 * The skeletal implementation of an option, which provides default
 * implementations for the basic accessor methods. Sub-classes must override the
 * abstract methods to provide functionality specific to their class type.
 * @author aito
 * 
 */
public abstract class AbstractOption implements Option {
  /** The name of this option. */
  private String name = "";
  /** The list of parameters used to execute this option. */
  private List<String> parameters = new ArrayList<String>();
  /** The controller containing additional information to execute this option. */
  private XmlDataController controller = null;

  /**
   * Constructs this option with a controller, option name, and a list of
   * parameters used to execute this option.
   * @param controller the specified controller.
   * @param name the specified option name.
   * @param parameters the specified parameters.
   */
  public AbstractOption(XmlDataController controller, String name, List<String> parameters) {
    this.controller = controller;
    this.name = name;
    this.parameters = parameters;
  }

  /** {@inheritDoc} */
  public String getName() {
    return this.name;
  }

  /** {@inheritDoc} */
  public List<String> getParameters() {
    return this.parameters;
  }

  /**
   * Implements the process method to provide the default process behavior,
   * which is perform no parameter processing.
   */
  public void process() { //NOPMD
    // Performs no processing.
  }

  /** {@inheritDoc} */
  public abstract boolean isValid();

  /**
   * Returns the controller which provides access to parameters processed in
   * other options and global sensor operations.
   * @return the controller instance.
   */
  public XmlDataController getController() {
    return this.controller;
  }

  /**
   * Implements the execute method with the default execute behavior, which is
   * to do nothing.
   */
  public void execute() { //NOPMD
    // Does no execution.
  }
}
