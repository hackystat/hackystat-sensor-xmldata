package org.hackystat.sensor.xmldata.option;

import java.util.List;

import org.hackystat.sensor.xmldata.XmlDataController;

/**
 * The option used to specify the resource associated with all of the entries
 * sent to the server.
 * @author aito
 * 
 */
public class ResourceOption extends AbstractOption {
  /** This option's name, which is "-resource". */
  public static final String OPTION_NAME = "-resource";

  /**
   * Creates this option with the specified controller and parameters.
   * @param controller the specified controller.
   * @param parameters the specified parameters.
   */
  public ResourceOption(XmlDataController controller, List<String> parameters) {
    super(controller, OPTION_NAME, parameters);
  }

  /** Processes this option by setting the sdt name found in this option. */
  @Override
  public void process() {
    if (this.isValid()) {
      this.getController().addOptionObject(Options.RESOURCE, this.getParameters().get(0));
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
          + " option must have only one argument.  Ex: -resource <resource>.";
      this.getController().fireMessage(msg);
      return false;
    }
    return true;
  }
}
