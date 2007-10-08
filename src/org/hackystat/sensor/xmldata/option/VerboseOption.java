package org.hackystat.sensor.xmldata.option;

import java.util.List;

import org.hackystat.sensor.xmldata.XmlDataController;

/**
 * The option called when the user wishes to display additional information that
 * is useful when debugging.
 * @author aito
 * 
 */
public class VerboseOption extends AbstractOption {
  /** The option name, which is "-verbose". */
  public static final String OPTION_NAME = "-verbose";

  /**
   * Creates this option with the specified controller and the specified list of
   * parameters.
   * @param controller the specified controller.
   * @param parameters the specified list of parameters.
   */
  public VerboseOption(XmlDataController controller, List<String> parameters) {
    super(controller, OPTION_NAME, parameters);
  }

  /** Processes this option by setting the verbose mode to true. */
  @Override
  public void process() {
    if (this.isValid()) {
      this.getController().addOptionObject(Options.VERBOSE, Boolean.TRUE);
    }
  }

  /**
   * Returns true if this option is valid. This option is valid if no parameters
   * are specified.
   * @return true if this argument has not parameters.
   */
  @Override
  public boolean isValid() {
    if (!this.getParameters().isEmpty()) {
      String msg = "The " + OPTION_NAME + " option does not accept arguments.  ";
      this.getController().fireMessage(msg);
      return false;
    }
    return true;
  }
}