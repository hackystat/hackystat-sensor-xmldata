package org.hackystat.sensor.xmldata.option;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.hackystat.sensor.xmldata.XmlDataController;

/**
 * The option used when specifying the command-line arguments via a text file.
 * @author aito
 * 
 */
public class ArgListOption extends AbstractOption {
  /** The name of this option, which is "-argList". */
  public static final String OPTION_NAME = "-argList";

  /**
   * Private constructor that creates this option with the specified controller,
   * name, and parameters.
   * @param controller the specified controller.
   * @param name the specified name.
   * @param parameters the specified parameters.
   */
  private ArgListOption(XmlDataController controller, String name, List<String> parameters) {
    super(controller, name, parameters);
  }

  /**
   * Constructs this option with the specified controller and parameters.
   * "-argList" is used as the name of this option.
   * @param controller the specified controller.
   * @param parameters the specified parameters.
   * @return the option instance.
   */
  public static Option createOption(XmlDataController controller, List<String> parameters) {
    Option option = new ArgListOption(controller, OPTION_NAME, parameters);
    return option;
  }

  /**
   * Returns true if the specified parameters contains only one element, which
   * is a valid text file containing the list of command-line arguments.
   * @return true if the parameters are valid, false if not.
   */
  @Override
  public boolean isValid() {
    if (this.getParameters().isEmpty() || this.getParameters().size() > 1) {
      String msg = "The -argList option only accepts one parameter, which "
          + "is the file containing the command-line arguments.";
      this.getController().fireMessage(msg);
      return false;
    }
    else if (!new File(this.getParameters().get(0)).exists()) {
      String msg = "The specified file, " + this.getParameters().get(0) + ", does "
          + "not exist.";
      this.getController().fireMessage(msg);
      return false;
    }

    return true;
  }

  /** Executes this option using the specified argument list file. */
  @Override
  public void execute() {
    List<String> arguments = new ArrayList<String>();
    try {
      Reader fileReader = new FileReader(this.getParameters().get(0));
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      String argumentLine = bufferedReader.readLine();
      String argumentString = "";
      // Compiles a string of the entire file.
      while (argumentLine != null) {
        argumentString = argumentString.concat(argumentLine + " ");
        argumentLine = bufferedReader.readLine();
      }

      StringTokenizer tokenizer = new StringTokenizer(argumentString);
      while (tokenizer.hasMoreTokens()) {
        arguments.add(tokenizer.nextToken());
      }
      bufferedReader.close();

      // Then, process the args string and execute the new list of arguments.
      this.getController().processArguments(arguments);
      this.getController().execute();
    }
    catch (FileNotFoundException e) {
      String msg = "The file, " + this.getParameters().get(0) + ", could not be found.";
      this.getController().fireMessage(msg, e.toString());
    }
    catch (IOException e) {
      String msg = "The file, " + this.getParameters().get(0) + ", could not be accessed.";
      this.getController().fireMessage(msg, e.toString());
    }
  }
}
