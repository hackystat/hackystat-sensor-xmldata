package org.hackystat.sensor.xmldata.option;

import java.util.ArrayList;
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
   * Private constructor that creates this option with the specified controller,
   * option name, and the specified list of parameters.
   * @param controller the specified controller.
   * @param name the specified name.
   * @param parameters the specified list of parameters.
   */
  private VerboseOption(XmlDataController controller, String name, List<String> parameters) {
    super(controller, name, parameters);
  }

  /**
   * Static factory method that creates this option with the specified
   * controller, "-verbose" as an option name, and no parameters.
   * @param controller the specified controller.
   * @return the option instance.
   */
  public static Option createOption(XmlDataController controller) {
    Option option = new VerboseOption(controller, OPTION_NAME, new ArrayList<String>());
    return option;
  }

  /** Processes this option by setting the verbose mode to true. */
  @Override
  public void process() {
    if (this.isValid()) {
      this.getController().setVerbose(true);
    }
  }

  /**
   * Always returns true because this options does not have an parameters.
   * @return always returns true.
   */
  @Override
  public boolean isValid() {
    return true;
  }

}