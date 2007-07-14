
package org.hackystat.sensor.xmldata.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.hackystat.sensor.xmldata.XmlDataController;
import org.hackystat.sensor.xmldata.XmlSensorException;

/**
 * The command that handles the "-argList" argument from the command-line interface.
 * 
 * @author Austen Ito
 * @version $Id:$
 * 
 */
public class ArgListCommand implements Command {
  /** The list of parameters associated with the "-file" argument */
  private List commandValues = new ArrayList();
  
  /**
   * Returns true if there is an args list file, false if not.
   * 
   * @return true if there is an argslist file, false if not.
   */
  public boolean validate () {
    if (this.commandValues.size() == 1) {
      String argsFile = (String) this.commandValues.get(0);
      if (new File(argsFile).exists() && new File(argsFile).canRead()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Executes this command.
   * 
   * @param control the <code>XmlDataController</code>.
   * @param commandValues list of parameters that is used by the -argList command.
   * 
   * @throws XmlSensorException thrown if this command cannot be executed.
   */
  public void execute (Object control, Object commandValues) throws XmlSensorException {
    XmlDataController contoller = (XmlDataController) control;
    this.commandValues = (ArrayList) commandValues;

    if (this.validate()) {
      List arguments = new ArrayList();
      try {
        BufferedReader reader = new BufferedReader(new FileReader((String) this.commandValues
            .get(0)));
        String argumentLine = reader.readLine();
        String argumentString = "";
        // compile a string of the entire file.
        while (argumentLine != null) {
          argumentString = argumentString.concat(argumentLine + " ");
          argumentLine = reader.readLine();
        }
        StringTokenizer tokenizer = new StringTokenizer(argumentString);
        while (tokenizer.hasMoreTokens()) {
          arguments.add((String) tokenizer.nextToken());
        }
        contoller.setCommandLineArguments(arguments);
        contoller.processCommands();
        reader.close();
      }
      catch (FileNotFoundException e) {
        XmlDataController.notifyCommandListeners(new CommandEvent("The arglist file"
            + " was not found.", false, true));
      }
      catch (IOException e) {
        XmlDataController.notifyCommandListeners(new CommandEvent("Error accessing the"
            + " arglist file.", false, true));
      }
    }
    else {
      String message = "The arglist file cannot be accessed or does not exist.";
      XmlDataController.notifyCommandListeners(new CommandEvent(message, false, true));
    }
  }
}
