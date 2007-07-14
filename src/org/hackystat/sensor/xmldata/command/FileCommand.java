
package org.hackystat.sensor.xmldata.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hackystat.core.kernel.shell.SensorShell;
import org.hackystat.sensor.xmldata.XmlDataController;
import org.hackystat.sensor.xmldata.XmlDataOutputParser;
import org.hackystat.sensor.xmldata.XmlSensorException;

/**
 * The command that handles the "-file" argument from the command-line interface.
 * 
 * @author Austen Ito
 * @version $Id:$
 * 
 */
public class FileCommand implements Command {
  /** The list of parameters associated with the "-file" argument */
  private List commandValues = new ArrayList();
  /** The output file name */
  /** The <code>XmlDataOutputParser</code> */
  private XmlDataOutputParser parser = null;
  private List validFileNames = new ArrayList();

  /**
   * Returns true if there are any output files that exist and can be read. If not this method
   * returns false.
   * 
   * @return true if there are output files that exist and can be read.
   */
  public boolean validate () {
    if (this.validFileNames.size() > 0) {
      return true;
    }
    return false;
  }

  /**
   * Executes this command.
   * 
   * @param parser the object that processes the xml output file.
   * @param commandValues list of parameters that is used by the -file command.
   * 
   * @throws XmlSensorException thrown if this command cannot be executed.
   */
  public void execute (Object parser, Object commandValues) throws XmlSensorException {
    this.commandValues = (ArrayList) commandValues;
    this.parser = (XmlDataOutputParser) parser;
    // gets all the valid files
    for (Iterator i = this.commandValues.iterator(); i.hasNext();) {
      String fileName = (String) i.next();
      File file = new File(fileName);
      if (file.exists() && file.canRead()) {
        this.validFileNames.add(fileName);
      }
      else {
        XmlDataController.notifyCommandListeners(new CommandEvent("The " + fileName
            + " output file is invalid.", false, false));
      }
    }

    if (this.validate()) {
      Date runtime = new Date(); // sets the runtime
      int entriesSent = 0;
      boolean isAllDataSent = true;
      for (Iterator i = this.validFileNames.iterator(); i.hasNext();) {
        String currentFileName = (String) i.next();
        entriesSent += this.parser.processXmlDataFile(currentFileName, runtime);
      }
      isAllDataSent = this.send();
      //if (entriesSent == 0) {
        //XmlDataController.notifyCommandListeners(new CommandEvent("Error: no entries sent.",
        //                                                          false, false));
      //}
      //else {
        XmlDataController.notifyCommandListeners(new CommandEvent(entriesSent, isAllDataSent));
      //}
    }
    else {
      String message = "There are no output files that exist or have data to send.";
      XmlDataController.notifyCommandListeners(new CommandEvent(message, false, false));
    }
  }

  /**
   * Returns true if all data is sent to the hackystat server, returns false if data from one or
   * more files is not sent.
   * 
   * @return true if all data is sent, false if not.
   */
  public boolean send () {
    boolean allDataSent = false;
    boolean isServerReachable = true;
    for (Iterator i = this.parser.getShells(); i.hasNext();) {
      SensorShell shell = (SensorShell) i.next();
      if (!shell.isServerPingable()) {
        isServerReachable = false;
      }
      allDataSent = shell.send();
    }
    if (!isServerReachable) {
      System.out.println("Server not available. Storing commands offline.");
    }
    return allDataSent;
  }

}
