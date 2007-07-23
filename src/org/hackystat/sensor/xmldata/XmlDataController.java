package org.hackystat.sensor.xmldata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hackystat.sensor.xmldata.option.FileOption;
import org.hackystat.sensor.xmldata.option.Option;
import org.hackystat.sensor.xmldata.option.SdtOption;

/**
 * The entry point for the command-line interface sensor and the "middle-man"
 * between the user interfaces and the <code>Command</code>s.
 * 
 * @author Austen Ito
 * @version $Id:$
 * 
 */
public class XmlDataController {
  /** True if the verbose mode is on, false if verbose mode is turned off. */
  private static boolean isVerbose = false;
  private List<Option> options = new ArrayList<Option>();

  /**
   * Constructs this controller with a list of arguments to process.
   * 
   * @param arguments the list of arguments.
   * @param observer the object that invokes this class. Usually the entry point
   * of the sensor.
   */
  public XmlDataController(List<String> arguments) {
    this.processCommands(arguments);
  }

  private void processCommands(List<String> arguments) {
    Map<String, List<String>> optionToArgs = new HashMap<String, List<String>>();
    String currentOption = "";

    // Iterate through all -option <arguments>... pairings to build a mapping of
    // option -> arguments.
    for (String argument : arguments) {
      // If the current string is an option flag, create a new mapping.
      if (this.isOption(argument)) {
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
        this.options.add(SdtOption.createSdtOption(entry.getKey(), entry.getValue()));
      }
      else if (FileOption.OPTION_NAME.equals(entry.getKey())) {
        this.options.add(FileOption.createSdtOption(entry.getKey(), entry.getValue()));
      }
    }
  }

  private boolean isOption(String argument) {
    if (argument.length() > 0) {
      String firstChar = argument.substring(0, 1);
      if ("-".equals(firstChar)) {
        return true;
      }
    }
    return false;
  }

  /** Executes the command entered by the user */
  public boolean execute() {
    if(this.isAllOptionsValid()){
        return true;
    }
    return false;
  }

  private boolean isAllOptionsValid() {
    for (Option option : this.options) {
      if (!option.isValid()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Sets if verbose mode is on. True sets verbose mode on, false turns verbose
   * mode off.
   * 
   * @param isVerbose true if verbose mode is on, false if not.
   */
  public static void setVerbose(boolean isVerbose) {
    XmlDataController.isVerbose = isVerbose;
  }

  /**
   * Returns if verbose mode is on. True means that verbose mode is on or false
   * means verbose mode is off.
   * 
   * @return True if verbose mode is on, false if not.
   */
  public static boolean isVerbose() {
    return XmlDataController.isVerbose;
  }
}
