package org.hackystat.sensor.xmldata.option;

import java.util.List;

import org.hackystat.sensor.xmldata.XmlDataController;

/**
 * The option that wraps the sensor data type information specified by the user.
 * 
 * @author Austen Ito
 * 
 */
public class SdtOption extends AbstractOption {
  /** This option's name, which is "-sdt". */
  public static final String OPTION_NAME = "-sdt";

  /**
   * Constructs this option with the specified controller, name, and parameters.
   * @param controller the specified controller.
   * @param name the specified name.
   * @param parameters the specified parameters.
   */
  private SdtOption(XmlDataController controller, String name, List<String> parameters) {
    super(controller, name, parameters);
  }

  /**
   * Static factory method that creates an option with the specified controller
   * and parameters. The name of this option is set to "-sdt".
   * @param controller the specified controller.
   * @param parameters the specified parameters.
   * @return the option instance.
   */
  public static Option createOption(XmlDataController controller, List<String> parameters) {
    Option option = new SdtOption(controller, OPTION_NAME, parameters);
    return option;
  }

  /** Processes this option by setting the sdt name found in this option. */
  @Override
  public void process() {
    if (this.isValid()) {
      this.getController().addOptionObject(Options.SDT, this.getParameters().get(0));
    }
  }

  /**
   * Returns true if the list of parameters contains only one element.
   * @return true if the parameters are valid, false if not.
   */
  @Override
  public boolean isValid() {
    if (this.getParameters().size() != 1) {
      String msg = "The " + OPTION_NAME
          + " option must have only one argument.  Ex: -sdt DevEvent.";
      this.getController().fireMessage(msg);
      return false;
    }
    return true;
  }
}
