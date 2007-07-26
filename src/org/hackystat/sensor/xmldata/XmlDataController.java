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
import org.hackystat.sensor.xmldata.option.VerboseOption;
import org.hackystat.sensorshell.SensorProperties;

/**
 * The class which parses the command-line arguments specified by the user,
 * validates the created options and their parameters, and executes the options.
 * @author Austen Ito
 * 
 */
public class XmlDataController {
  /** True if the verbose mode is on, false if verbose mode is turned off. */
  private boolean isVerbose = false;
  /** The sensor data type name used by all data sent to the sensorbase. */
  private String sdtName = "";
  /** The class that manages the options created from the user's arguments. */
  private OptionHandler optionHandler = null;
  /** The class which encapsulates the firing of messages. */
  private MessageDelegate messageDelegate = null;

  /**
   * Constructs this controller with a list of arguments to process.
   * 
   * @param arguments the list of arguments.
   */
  public XmlDataController(List<String> arguments) {
    this.optionHandler = new OptionHandler(this);
    this.messageDelegate = new MessageDelegate(this.isVerbose);
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
      String optionName = entry.getKey();
      List<String> optionParams = entry.getValue();

      if (SdtOption.OPTION_NAME.equals(optionName)) {
        Option sdtOption = SdtOption.createOption(this, optionParams);
        this.optionHandler.addOption(sdtOption);
        if (!sdtOption.getParameters().isEmpty()) {
          this.sdtName = entry.getValue().get(0);
        }
      }
      else if (FileOption.OPTION_NAME.equals(optionName)) {
        this.optionHandler.addOption(FileOption.createOption(this, optionParams));
      }
      else if (VerboseOption.OPTION_NAME.equals(optionName)) {
        this.isVerbose = true;
        this.optionHandler.addOption(VerboseOption.createOption(this));
      }
      else {
        this.fireMessage("The '" + entry.getKey() + "' option is not supported.");
      }
    }
    this.messageDelegate = new MessageDelegate(this.isVerbose);
  }

  /** Executes all of the options specified by the user */
  public void execute() {
    SensorProperties properties = new SensorProperties();
    this.fireVerboseMessage("Hackystat Host: " + properties.getHackystatHost());
    if (this.optionHandler.isOptionsValid() && this.optionHandler.hasRequiredOptions()) {
    }
  }

  /**
   * Displays the specified message. The same message is displayed even if the
   * verbose option is enabled.
   * @param message the specified message to display.
   */
  public void fireMessage(String message) {
    this.messageDelegate.fireMessage(message);
  }

  /**
   * Displays the specified message if verbose mode is enabled.
   * @param message the specified message to display.
   */
  public void fireVerboseMessage(String message) {
    this.messageDelegate.fireVerboseMessage(message);
  }

  /**
   * Displays the specified message is verbose mode is disabled or the verbose
   * message if verbose mode is enabled.
   * @param message the specified message.
   * @param verboseMessage the specified verbose message.
   */
  public void fireMessage(String message, String verboseMessage) {
    this.messageDelegate.fireMessage(message, verboseMessage);
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
