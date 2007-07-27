package org.hackystat.sensor.xmldata.option;

import java.util.List;

import org.hackystat.sensor.xmldata.XmlDataController;

/**
 * The class which follows the factory pattern to encapsulate the creation of
 * options based on the specified parameters.
 * @author aito
 * 
 */
public class OptionFactory {
  /** Private constructor to prevent instantiation. */
  private OptionFactory() {
  }

  /**
   * Returns an option instance based on the specified option name and
   * parameters. If an option cannot be created, null is returned.
   * @param controller the specified controller used to create an option.
   * @param optionName the option name.
   * @param parameters the option parameters.
   * @return the option instance if it can be created, or null if an option
   * instance cannot be created using the specified arguments.
   */
  public static Option getInstance(XmlDataController controller, String optionName,
      List<String> parameters) {

    if (SdtOption.OPTION_NAME.equals(optionName)) {
      return SdtOption.createOption(controller, parameters);
    }
    else if (FileOption.OPTION_NAME.equals(optionName)) {
      return FileOption.createOption(controller, parameters);
    }
    else if (VerboseOption.OPTION_NAME.equals(optionName)) {
      return VerboseOption.createOption(controller);
    }
    else {
      controller.fireMessage("The '" + optionName + "' option is not supported.");
    }
    return null;
  }
}
