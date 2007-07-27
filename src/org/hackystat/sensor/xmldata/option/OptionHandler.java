package org.hackystat.sensor.xmldata.option;

import java.util.ArrayList;
import java.util.List;

import org.hackystat.sensor.xmldata.XmlDataController;

/**
 * The class that handles the options created by the XmlDataController. This
 * class stores, validates, and executes the options.
 * @author aito
 * 
 */
public class OptionHandler {
  /** The list of options managed by this class. */
  private List<Option> options = new ArrayList<Option>();
  /** The controller which loads this handler. */
  private XmlDataController controller = null;

  /**
   * Constructs this handler with the specified controller.
   * @param controller the specified controller.
   */
  public OptionHandler(XmlDataController controller) {
    this.controller = controller;
  }

  /**
   * Adds the specified option to this class. Note that no error checking is
   * performed by this method. Options may be tested by invoking isOptionsValid.
   * @param option the specified option to add.
   */
  public void addOption(Option option) {
    this.options.add(option);
  }

  /**
   * Returns true if all of the options stored in this class are valid and
   * contain no duplicate options. If an option is invalid, an error message is
   * fired by the invalid option.
   * @return true if the option is valid, false if not.
   */
  public boolean isOptionsValid() {
    // First, iterate through each option and validate their parameters.
    List<String> optionNames = new ArrayList<String>();
    for (Option option : this.options) {
      optionNames.add(option.getName());
      if (!option.isValid()) {
        return false;
      }
    }

    // Second, iterate through all names to see if each option is unique.
    for (String optionName : optionNames) {
      boolean hasName = false;
      for (Option option : this.options) {
        if (option.getName().equals(optionName)) {
          if (hasName) {
            return false;
          }
          hasName = true;
        }
      }
    }
    return true;
  }

  /**
   * Returns true if this class contains all of the required options.
   * @return true if all required options exist.
   */
  public boolean hasRequiredOptions() {
    boolean hasSdtOption = this.hasOptionWithName(SdtOption.OPTION_NAME);
    boolean hasFileOption = this.hasOptionWithName(FileOption.OPTION_NAME);
    if (!hasSdtOption) {
      String msg = "The -sdt <sdt name> option is required.";
      this.controller.fireMessage(msg);
      return false;
    }
    else if (!hasFileOption) {
      String msg = "The -file <files...> option is required.";
      this.controller.fireMessage(msg);
      return false;
    }
    return true;
  }

  /**
   * Returns true if this class contains the option with the specified name.
   * @param name the name to search for.
   * @return true if the name was found, false if not.
   */
  private boolean hasOptionWithName(String name) {
    for (Option option : this.options) {
      if (option.getName().equals(name)) {
        return true;
      }
    }
    return false;
  }

  /**
   * A helper method that returns true if the specified string is an option.
   * @param argument the string to test.
   * @return true if the string is an option, false if not.
   */
  public boolean isOption(String argument) {
    if (argument != null && argument.length() > 0) {
      String firstChar = argument.substring(0, 1);
      if ("-".equals(firstChar)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Executes all of the stored options that have operations to execute on their
   * wrapped parameters.
   */
  public void execute() {
    for (Option option : this.options) {
      if (option instanceof Executable) {
        Executable executable = (Executable) option;
        executable.execute();
      }
    }
  }
}
