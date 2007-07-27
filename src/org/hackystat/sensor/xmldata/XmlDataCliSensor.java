package org.hackystat.sensor.xmldata;

import java.util.Arrays;

/**
 * The entry point into the XmlDataSensor. This class allows command-line
 * arguments to be specified by the user to perform actions based on the options
 * and their parameters.
 * 
 * @author Austen Ito
 * 
 */
public class XmlDataCliSensor {
  /** A summary usage message. */
  static final String[] USAGE_MSG = { "\nUsage:\n " + "[-verbose <true|false> "
      + "[-createRunTime <name>] [-sdt <name>]"
      + " [-nameMap <oldAttribute> <newAttribute>]\n"
      + "  -file <filename> [filename]... or -argList <filename>\n"
      + "\n\nNote: optional arguments are within square brackets. "
      + "Arguments can be used in any order." };

  /**
   * Provide the command line interface to the XmlData sensor.
   * 
   * @param args The command line arguments.
   */
  public static void main(String[] args) {
    try {
      if (args.length == 0) {
        XmlDataCliSensor sensor = new XmlDataCliSensor();
        sensor.usage();
      }
      else {
        XmlDataController controller = new XmlDataController(Arrays.asList(args));
        controller.execute();
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      System.out.println("A fatal error has occured.  Please contact "
          + "your hackystat administrator.");
    }
  }

  /**
   * Display a usage summary message on System.err and exit.
   */
  private void usage() {
    for (int i = 0; i < USAGE_MSG.length; i++) {
      System.err.println(USAGE_MSG[i]);
    }
  }
}
