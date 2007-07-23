//
//package org.hackystat.sensor.xmldata.option;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import org.hackystat.sensor.xmldata.XmlDataController;
//import org.hackystat.sensor.xmldata.XmlSensorException;
//
///**
// * Implements the "-createRunTime" option. This option creates a runtime attribute with a specified
// * attribute name.
// * 
// * @author Austen Ito
// * @version $Id:$
// * 
// */
//public class CreateRunTimeOption implements Option {
//  /** Parameters needed by this option */
//  private List parameters = null;
//
//  /**
//   * Returns true if the parameters contains only one value.
//   * 
//   * @return true if the list of attributes is valid.
//   */
//  public boolean validate () {
//    if (this.parameters.size() == 1) {
//      return true;
//    }
//    return false;
//  }
//
//  /**
//   * Sets the runTimeString and value in the <code>XmlDataOutputParser</code>.
//   * 
//   * @param parser the class that processes the output file.
//   * @param parameters the parameters needed by this option
//   * @throws XmlSensorException thrown if the option cannot be set.
//   */
//  public void set (Object parameters) throws XmlSensorException {
//    this.parameters = (ArrayList) parameters;
//    if (this.validate()) {
//      String runTimeString = (String) this.parameters.get(0);
//      String runTimeValue = String.valueOf(new Date().getTime()); 
////      parser.setRunTime(runTimeString, runTimeValue);
////      parser.changed("Runtime name is: " + runTimeString + ", value is: " + runTimeValue);
//    }
//  }
//}
