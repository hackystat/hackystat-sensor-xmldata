package org.hackystat.sensor.xmldata.option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hackystat.sensor.xmldata.XmlDataController;
import org.hackystat.sensor.xmldata.XmlDataOutputParser;
import org.hackystat.sensor.xmldata.XmlSensorException;

/**
 * Implements the "-nameMap" option. This options allows different attribute names to be used in the
 * xml output file, while still being compatiable with the required fields needed by hackystat
 * sensor data types. The key of the namemap is the old value that is needed by hackystat sensor
 * data types, which is mapped to the new attribute value that is in the xml output file.
 * 
 * @author Austen Ito
 * @version $Id:$
 * 
 */
public class NameMapOption implements Option {
  /** Parameters needed by this option */
  private List parameters = null;

  /**
   * Returns true if the list of attributes contains the correct amount to map old attributes to new
   * attributes/
   * 
   * @return true if the list of attributes is valid.
   */
  public boolean validate () {
    if (this.parameters.size() % 2 == 0) {
      return true;
    }
    return false;
  }

  /**
   * Sets the nameMap in the <code>XmlDataOutputParser</code>.
   * 
   * @param parser the class that processes the output file.
   * @param parameters the parameters needed by this option
   * @throws XmlSensorException thrown if the option cannot be set.
   */
  public void set (XmlDataOutputParser parser, Object parameters) throws XmlSensorException {
    this.parameters = (ArrayList) parameters;
    if (this.validate()) {
      StringBuffer buffer = new StringBuffer("Namemap Key-Val Pairs: ");
      HashMap nameMap = new HashMap();
      // populates the name map
      for (Iterator i = this.parameters.iterator(); i.hasNext();) {
        String key = (String) i.next();
        String value = (String) i.next();
        nameMap.put(value, key);
        buffer.append(key + "=" + value + ", " );
      }
      parser.setNameMap(nameMap);
      //removes the last comma and space from the output string
      parser.changed(buffer.toString().substring(0, buffer.toString().length() - 2));
    }
    else {
      XmlDataController.notifyOptionListeners(new OptionEvent("Failed to set the attribute "
          + "name mapping.", false));
    }
  }
}
