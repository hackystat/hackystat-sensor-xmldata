package org.hackystat.sensor.xmldata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hackystat.sensor.xmldata.option.FileOption;
import org.hackystat.sensor.xmldata.option.Option;
import org.hackystat.sensor.xmldata.option.OptionHandler;
import org.hackystat.sensor.xmldata.option.SdtOption;

/**
 * The class which parses the command-line arguments specified by the user,
 * validates the created options and their parameters, and executes the options.
 * @author Austen Ito
 * 
 */
public class XmlDataController {
  /** True if the verbose mode is on, false if verbose mode is turned off. */
  private static boolean isVerbose = false;
  /** The sensor data type name used by all data sent to the sensorbase. */
  private String sdtName = "";
  /** The class that manages the options created from the user's arguments. */
  private OptionHandler optionHandler = new OptionHandler();

  /**
   * Constructs this controller with a list of arguments to process.
   * 
   * @param arguments the list of arguments.
   */
  public XmlDataController(List<String> arguments) {
    this.processCommands(arguments);
  }

  /**
   * This method parses the specified command-line arguments and creates
   * options, which can be validated and executed.
   * @param arguments the list of arguments to parse.
   */
  private void processCommands(List<String> arguments) {
    Map<String, List<String>> optionToArgs = new HashMap<String, List<String>>();
    String currentOption = "";

    // Iterate through all -option <arguments>... pairings to build a mapping of
    // option -> arguments.
    for (String argument : arguments) {
      // If the current string is an option flag, create a new mapping.
      if (this.optionHandler.isOption(argument)) {
        optionToArgs.put(argument, new ArrayList<String>());
        currentOption = argument;
      }
      // Else, add the argument to the option flag list.
      else {
        List<String> args = optionToArgs.get(currentOption);
        args.add(argument);
        optionToArgs.put(currentOption, args);
      }
    }

    // Next, let's create the options using the command-line information.
    for (Entry<String, List<String>> entry : optionToArgs.entrySet()) {
      if (SdtOption.OPTION_NAME.equals(entry.getKey())) {
        Option sdtOption = SdtOption.createOption(this, entry.getValue());
        this.optionHandler.addOption(sdtOption);
        if (sdtOption.isValid()) {
          this.sdtName = entry.getValue().get(0);
        }
      }
      else if (FileOption.OPTION_NAME.equals(entry.getKey())) {
        this.optionHandler.addOption(FileOption.createOption(this, entry.getValue()));
      }
    }
  }

  /** Executes all of the options specified by the user */
  public void execute() {
  }
  
  /**
   * Returns if verbose mode is on. True means that verbose mode is on or false
   * means verbose mode is off.
   * 
   * @return True if verbose mode is on, false if not.
   */
  public boolean isVerbose() {
    return XmlDataController.isVerbose;
  }

  /**
   * Returns the sensor data type name that is associated with all data sent via
   * sensorshell to the sensorbase.
   * @return the sensor data type name.
   */
  public String getSdtName() {
    return this.sdtName;
  }
}
