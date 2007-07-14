
package org.hackystat.sensor.xmldata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hackystat.sensor.xmldata.command.Command;
import org.hackystat.sensor.xmldata.command.CommandEvent;
import org.hackystat.sensor.xmldata.command.CommandListener;
import org.hackystat.sensor.xmldata.option.Option;
import org.hackystat.sensor.xmldata.option.OptionEvent;
import org.hackystat.sensor.xmldata.option.OptionListener;

/**
 * The entry point for the command-line interface sensor and the "middle-man" between the user
 * interfaces and the <code>Command</code>s.
 * 
 * @author Austen Ito
 * @version $Id:$
 * 
 */
public class XmlDataController {
  /** List of <code>CommandListeners</code>. */
  private static List commandListeners = new ArrayList();
  /** List of <code>OptionListeners</code> */
  private static List optionListeners = new ArrayList();
  /** Parses the xml output files */
  private static XmlDataOutputParser outputParser;
  /** The list of valid command-line options */
  private String[] validOptions = { "verbose", "createRunTime", "sdt", "nameMap" };
  /** The map of oldAttributeValues and newAttributeValues */
  public static HashMap nameMap = new HashMap();
  /** The list of valid commands */
  public String[] validCommands = { "file", "fileList", "argList" };
  /** The list of command-line arguments */
  private List commandLineArguments = new ArrayList();
  /** True if the verbose mode is on, false if verbose mode is turned off. */
  private static boolean isVerbose = false;
  /** The list of observers */
  private static List observers = new ArrayList();
  /** The command that will be executed */
  private String command = "";
  /** The values associated with the command */
  private List commandValues = new ArrayList();

  /**
   * Constructs this controller with a list of arguments to process.
   * 
   * @param arguments the list of arguments.
   * @param observer the object that invokes this class. Usually the entry point of the sensor.
   */
  public XmlDataController(Object observer, List arguments) {
    this.commandLineArguments = arguments;
    XmlDataController.observers.add(observer);
    XmlDataController.outputParser = new XmlDataOutputParser();
  }

  /**
   * Processes the <code>Command</code>s and <code>Option</code>s that are provided by the
   * user.
   * 
   * @throws IOException thrown if there is a problem parsing the argList file.
   */
  public void processCommands () throws IOException {
    HashMap options = new HashMap();
    boolean hasCommand = false;
    if (this.commandLineArguments.size() == 0) {
      notifyCommandListeners(new CommandEvent("Invalid arguments.", false, true));
    }

    /*
     * Places each set of arguments, which is marked by a -<command> <values...>, into it's own
     * list.
     */
    List arguments = new ArrayList();
    for (int i = 0; i < this.commandLineArguments.size(); i++) {
      List list = new ArrayList();
      String argument = (String) this.commandLineArguments.get(i);
      if (argument.startsWith("-")) { // command or option denoted with a "-"
        list.add(argument.substring(1));
        // traverses through the values associated with a command/option
        for (int j = (i + 1); j < this.commandLineArguments.size(); j++) {
          String value = (String) this.commandLineArguments.get(j);
          if (!value.startsWith("-")) {
            list.add(value);
            if (j + 1 == this.commandLineArguments.size()) {
              i = j + 1;
              arguments.add(list);
            }
          }
          else {
            i = j - 1;
            arguments.add(list);
            break;
          }
        }
        if (list.size() == 1) {
          notifyCommandListeners(new CommandEvent("The " + argument + " "
              + "argument must have values associated with it.", false, true));
          break;
        }
      }
      else {
        notifyCommandListeners(new CommandEvent("Invalid arguments.", false, true));
      }
    }

    // traverses through the lists of arguments
    for (Iterator i = arguments.iterator(); i.hasNext();) {
      List argumentList = (ArrayList) i.next();
      String argument = "";
      List values = new ArrayList();
      int count = 0;
      for (Iterator j = argumentList.iterator(); j.hasNext();) {
        String value = (String) j.next();
        if (count == 0) {
          argument = value; // is a command or option
        }
        else {
          values.add(value); // value associated with a command
        }
        count++;
      }
      if (isCommand(argument)) {
        if (hasCommand) { // only one command is allowed.
          notifyCommandListeners(new CommandEvent("Error: only one command allowed.", false, true));
        }
        else {
          hasCommand = true;
          this.command = argument;
          this.commandValues.addAll(values); // populates command values
        }
      }
      else if (isOption(argument)) {
        if ("nameMap".equals(argument) && options.keySet().contains("nameMap")) {
          List nameMaps = (ArrayList) options.get("nameMap");
          values.addAll(nameMaps);
        }
        options.put(argument, values); // populates option values
      }
      else { // invalid arguments
        notifyCommandListeners(new CommandEvent("The " + argument + " argument is invalid.",
                                                false, true));
      }
    }
    if ("".equals(this.command)) { // no -file or -argList command
      notifyCommandListeners(new CommandEvent("A -file or -argList command must be provided.",
                                              false, true));
    }
    // sets options
    if (options.size() > 0) {
      this.setOptions(options);
    }
  }

  /** Executes the command entered by the user */
  public void execute () {
    executeCommand(this.command, this.commandValues);
  }

  /**
   * Executes a <code>Command</code>.
   * 
   * @param commandName the command name.
   * @param commandValues the parameters associated with the command.
   */
  private void executeCommand (String commandName, List commandValues) {
    try {
      String commandClassName = "org.hackystat.sensor.xmldata.command."
          + commandName.substring(0, 1).toUpperCase() + commandName.substring(1) + "Command";
      Command command = (Command) Class.forName(commandClassName).newInstance();
      if ("file".equals(commandName)) {
        command.execute(XmlDataController.outputParser, commandValues);
      }
      else if ("argList".equals(commandName)) {
        command.execute(this, commandValues);
      }
      // Clear listeners so that duplicate messages are not printed.
      XmlDataController.commandListeners.clear();
      XmlDataController.optionListeners.clear();
      XmlDataController.observers.clear();
    }
    catch (Exception e) {
      String message = "A fatal error has occurred, please contact your Hackystat Administrator: ";
      System.err.println(message + e.getMessage());
    }
  }

  /**
   * Sets all of the options provided by the user.
   * 
   * @param options the map that contains the options to be set
   */
  private void setOptions (HashMap options) {
    String optionName = "";
    try {
      for (Iterator i = options.keySet().iterator(); i.hasNext();) {
        optionName = (String) i.next();
        String optionClassName = "org.hackystat.sensor.xmldata.option."
            + optionName.substring(0, 1).toUpperCase() + optionName.substring(1) + "Option";
        Option option = (Option) Class.forName(optionClassName).newInstance();
        option.set(XmlDataController.outputParser, options.get(optionName));
      }
    }
    catch (Exception e) {
      System.err.println("Error setting the " + optionName + " option.");
    }
  }

  /**
   * Returns true if the string is a valid command. Returns false if not.
   * 
   * @param command the command to validate.
   * @return true if the command is valid, false if not.
   */
  private boolean isCommand (String command) {
    for (Iterator i = java.util.Arrays.asList(this.validCommands).iterator(); i.hasNext();) {
      String validCommand = (String) i.next();
      if (validCommand.equals(command)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns true if the option entered at the command-line is valid. Returns false if not.
   * 
   * @param option the option entered by the user.
   * @return true if the option is valid, false if not.
   */
  private boolean isOption (String option) {
    for (Iterator i = java.util.Arrays.asList(this.validOptions).iterator(); i.hasNext();) {
      String validOption = (String) i.next();
      if (validOption.equals(option)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Notifys the <code>CommandListener</code>s of an event that was fired.
   * 
   * @param e the event fired.
   */
  public static void notifyCommandListeners (CommandEvent e) {
    for (Iterator i = commandListeners.iterator(); i.hasNext();) {
      CommandListener listener = (CommandListener) i.next();
      listener.commandPerformed(e);
    }
  }

  /**
   * Notifys the <code>OptionListener</code>s of an event that was fired.
   * 
   * @param e the event fired.
   */
  public static void notifyOptionListeners (OptionEvent e) {
    for (Iterator i = optionListeners.iterator(); i.hasNext();) {
      OptionListener listener = (OptionListener) i.next();
      listener.setOptionPerformed(e);
    }
  }

  /**
   * Adds <code>CommandListener</code>s to to this class.
   * 
   * @param listener the <code>CommandListener</code>.
   */
  public static void addCommandListeners (CommandListener listener) {
    commandListeners.add(listener);
  }

  /**
   * Is used when running unit tests so that values from seperate test cases are not mixed.
   * 
   * @param listener the <code>CommandListener</code>.
   * @return true if the listener was removed, false if not.
   */
  public static boolean removeCommandListeners (CommandListener listener) {
    return commandListeners.remove(listener);
  }

  /**
   * Adds <code>OptionListener</code>s to to this class.
   * 
   * @param listener the <code>OptionListener</code>.
   */
  public static void addOptionListeners (OptionListener listener) {
    optionListeners.add(listener);
  }

  /**
   * Is used when running unit tests so that values from seperate test cases are not mixed.
   * 
   * @param listener the <code>OptionListener</code>.
   * @return true if the listener was removed, false if not.
   */
  public static boolean removeOptionListeners (OptionListener listener) {
    return optionListeners.remove(listener);
  }

  /**
   * Sets the command-line arguments.
   * 
   * @param commandLineArguments the command-line arguments.
   */
  public void setCommandLineArguments (List commandLineArguments) {
    this.commandLineArguments = commandLineArguments;
  }

  /**
   * Returns the command-line arguments.
   * 
   * @return the command-line arguments.
   */
  public List getCommandLineArguments () {
    return commandLineArguments;
  }

  /**
   * Sets if verbose mode is on. True sets verbose mode on, false turns verbose mode off.
   * 
   * @param isVerbose true if verbose mode is on, false if not.
   */
  public static void setVerbose (boolean isVerbose) {
    XmlDataController.isVerbose = isVerbose;
  }

  /**
   * Returns if verbose mode is on. True means that verbose mode is on or false means verbose mode
   * is off.
   * 
   * @return True if verbose mode is on, false if not.
   */
  public static boolean isVerbose () {
    return XmlDataController.isVerbose;
  }

  /**
   * Returns an iterator of a list of <code>Observers</code>.
   * 
   * @return the iterator of a list of observers.
   */
  public static Iterator getObservers () {
    return XmlDataController.observers.iterator();
  }
}
