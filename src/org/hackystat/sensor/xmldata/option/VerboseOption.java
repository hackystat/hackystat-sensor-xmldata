//
// package org.hackystat.sensor.xmldata.option;
//
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.Iterator;
// import java.util.List;
//
// import org.hackystat.sensor.xmldata.XmlDataController;
// import org.hackystat.sensor.xmldata.XmlSensorException;
//
// /**
// * The option that sets the verbose mode to be on or off. When verbose mode is
// on, additional
// * information is displayed to the user such as the current file being parsed.
// *
// * @author Austen Ito
// * @version $Id:$
// *
// */
// public class VerboseOption implements Option {
// /** Parameters needed by this option */
// private List parameters = null;
// private String[] verboseValuesArray = { "true", "false", "yes", "no", "on",
// "off" };
//
// /**
// * Returns true if the parameters contains only one value and the value is
// either 'true', 'yes',
// * 'no' or their opposite values.
// *
// * @return true if the list of attributes is valid.
// */
// public boolean validate () {
// if (this.parameters.size() == 1) {
// String verboseValue = (String) this.parameters.get(0);
// List verboseValueList = Arrays.asList(this.verboseValuesArray);
// for (Iterator i = verboseValueList.iterator(); i.hasNext();) {
// String value = (String) i.next();
// if (verboseValue.equalsIgnoreCase(value)) {
// return true;
// }
// }
// }
// return false;
// }
//
// /**
// * Sets if verbose mode is on in the <code>XmlDataOutputParser</code>.
// *
// * @param parser the class that processes the output file.
// * @param parameters the parameters needed by this option
// * @throws XmlSensorException thrown if the option cannot be set.
// */
// public void set (Object parameters) throws XmlSensorException {
// this.parameters = (ArrayList) parameters;
// if (this.validate()) {
// XmlDataController.setVerbose(this.verboseToBoolean((String)
// this.parameters.get(0)));
// // parser.changed("Verbose mode on: " + XmlDataController.isVerbose());
// }
// }
//
// /**
// * Returns true if the String accepted is 'true', 'on', or 'yes'. Returns
// false if any other value
// * is accepted. This method was written as a replacement for the Ant method
// Project.toBoolean().
// * The Command-Line Interface would require the ant.jar library in order to
// support the
// * Project.toBoolean() method, so I created a little hack to mimic its
// features.
// *
// * @param verboseString the verbose string.
// * @return true if verbose is to be turned on, false if not.
// */
// private boolean verboseToBoolean (String verboseString) {
// if (verboseString.equalsIgnoreCase("true") ||
// verboseString.equalsIgnoreCase("on")
// || verboseString.equalsIgnoreCase("yes")) {
// return true;
// }
// return false;
// }
// }
