package org.hackystat.sensor.xmldata.option;

import java.util.List;

import org.hackystat.sensor.xmldata.XmlDataController;

/**
 * The option used to mark all data sent by this sensor as a batch of data. A
 * batch of data is marked by having the same runtime.
 * @author aito
 * 
 */
public class SetRuntimeOption extends AbstractOption {
  /** This option's name, which is "-setRuntime". */
  public static final String OPTION_NAME = "-setRuntime";

  /**
   * Creates this option with the specified controller and parameters.
   * @param controller the specified controller.
   * @param parameters the specified parameters.
   */
  public SetRuntimeOption(XmlDataController controller, List<String> parameters) {
    super(controller, OPTION_NAME, parameters);
  }

  /** Processes this option by setting the runtime option to true. */
  @Override
  public void process() {
    if (this.isValid()) {
      this.getController().addOptionObject(Options.SET_RUNTIME, Boolean.TRUE);
    }
  }

  /**
   * Returns true if the list of parameters contains only no elements.
   * @return true if the parameters are valid, false if not.
   */
  @Override
  public boolean isValid() {
    if (this.getParameters().size() == 0) {
      return true;
    }
    String msg = "The " + OPTION_NAME + " option must have no arguments.  Ex: -setRuntime.";
    this.getController().fireMessage(msg);
    return false;
  }
}
