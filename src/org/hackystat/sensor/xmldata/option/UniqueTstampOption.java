package org.hackystat.sensor.xmldata.option;

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
   * Creates this option with the specified controller and the specified list of
   * parameters.
   * @param controller the specified controller.
   * @param parameters the specified list of parameters.
   */
  public UniqueTstampOption(XmlDataController controller, List<String> parameters) {
    super(controller, OPTION_NAME, parameters);
  }

  /** Processes this option by setting the unique timestamps mode to true. */
  @Override
  public void process() {
    if (this.isValid()) {
      this.getController().addOptionObject(Options.UNIQUE_TSTAMP, Boolean.TRUE);
    }
  }

  /**
   * Returns true if the list of parameters contains no parameters.
   * @return true if this option has no parameters, false if not.
   */
  @Override
  public boolean isValid() {
    if (!this.getParameters().isEmpty()) {
      String msg = "The " + OPTION_NAME + " option does not accept parameters.  ";
      this.getController().fireMessage(msg);
      return false;
    }
    return true;
  }
}
