package org.hackystat.sensor.xmldata.option;

import java.util.ArrayList;
import java.util.List;

import org.hackystat.core.kernel.sdt.SdtManager;
import org.hackystat.core.kernel.sdt.SensorDataType;
import org.hackystat.sensor.xmldata.XmlDataController;
import org.hackystat.sensor.xmldata.XmlDataOutputParser;
import org.hackystat.sensor.xmldata.XmlSensorException;

/**
 * Implements the "-sdt" option. Sets the sdt to be what the user specifies, but does not override
 * the sdt definition in output files.
 * 
 * @author Austen Ito
 * @version $Id:$
 * 
 */
public class SdtOption implements Option {
  /** Parameters needed by this option */
  private List parameters = null;
  /** The name of the sensor data type */
  private String sdtName = "";

  /**
   * Returns true if the SDT is valid.
   * 
   * @return true if the SDT is valid.
   */
  public boolean validate () {
    if (this.parameters.size() != 1) {
      return false;
    }
    this.sdtName = (String) this.parameters.get(0);
    SensorDataType sdt = SdtManager.getInstance().getSdt(this.sdtName);
    if (sdt == null) {
      return false;
    }
    return true;
  }

  /**
   * Sets the sensor data type.
   * 
   * @param parser the class that processes the output file.
   * @param parameters the parameters needed by this option
   * @throws XmlSensorException thrown if the option cannot be set.
   */
  public void set (XmlDataOutputParser parser, Object parameters) throws XmlSensorException {
    this.parameters = (ArrayList) parameters;
    if (this.validate()) {
      parser.setSdtName(this.sdtName);
    }
    else {
      XmlDataController.notifyOptionListeners(new OptionEvent("Failed to set the " + this.sdtName
          + " sensor data type.  Files that do not require the "
          + "sensor data type will still have their data sent.", false));
    }
  }
}
