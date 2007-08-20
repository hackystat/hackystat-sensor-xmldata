package org.hackystat.sensor.xmldata.option;

import java.util.ArrayList;
import java.util.List;

import org.hackystat.sensor.xmldata.XmlDataController;

/**
 * The option used to alter the "Timestmap" attribute in the sensor data files
 * to ensure uniqueness. This removes the data collision problem due to entries
 * having the same timstamps, but the cost is that the sensor/client-side will
 * lose information about what timestamps are actually being sent to the server.
 * @author aito
 * 
 */
public class UniqueTstampOption extends AbstractOption {
  /** The option name, which is "-uniqueTimestamps". */
  public static final String OPTION_NAME = "-uniqueTimestamps";

  /**
   * Private constructor that creates this option with the specified controller,
   * option name, and the specified list of parameters.
   * @param controller the specified controller.
   * @param name the specified name.
   * @param parameters the specified list of parameters.
   */
  private UniqueTstampOption(XmlDataController controller, String name, List<String> parameters) {
    super(controller, name, parameters);
  }

  /**
   * Static factory method that creates this option with the specified
   * controller, "-uniqueTimestamps" as an option name, and no parameters.
   * @param controller the specified controller.
   * @return the option instance.
   */
  public static Option createOption(XmlDataController controller) {
    Option option = new UniqueTstampOption(controller, OPTION_NAME, new ArrayList<String>());
    return option;
  }

  /** Processes this option by setting the unique timestamps mode to true. */
  @Override
  public void process() {
    if (this.isValid()) {
      this.getController().addOptionObject(Options.UNIQUE_TSTAMP, Boolean.TRUE);
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
