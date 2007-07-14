
package org.hackystat.sensor.xmldata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.hackystat.core.kernel.admin.SensorProperties;
import org.hackystat.core.kernel.admin.ServerProperties;
import org.hackystat.core.kernel.sdt.EntryAttribute;
import org.hackystat.core.kernel.sdt.SdtManager;
import org.hackystat.core.kernel.sdt.SensorDataType;
import org.hackystat.core.kernel.sensordata.SensorDataPropertyMap;
import org.hackystat.core.kernel.shell.SensorShell;
import org.hackystat.core.kernel.user.UserManager;
import org.hackystat.sensor.xmldata.command.CommandEvent;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Parses the XmlData output file and sets up a <code>SensorShell</code> object with the metric
 * data extracted. Each file populates a new <code>SensorShell</code> object.
 * 
 * @author Austen Ito, Aaron A. Kagawa
 * @version $Id:$
 */
public class XmlDataOutputParser extends Observable {
  /** The sensor shell instance used by this sensor. */
  private SensorShell shell;
  /** The list of <code>SensorShell</code> objects that store sensor data. */
  private List shells = new ArrayList();
  /** The current list of attributes required by the SDT */
  private List sdtRequiredAttributes = new ArrayList();
  /** Is used to decide if the XmlDataSensor is being tested */
  private boolean isTesting = false;
  /** Whether or not to add a runTime attribute as required or optional field. */
  private boolean createRunTime = false;
  /** The runtime string name to be used if createRunTime is enabled. */
  private String runTimeName = null;
  /** The runtime value to be used if createRunTime is enabled. */
  private String runTimeValue = null;
  /** The sdt name that the output file is associated with */
  private String sdtName = null;
  /**
   * The map that contains the attribute name in the output file, which is associated with the name
   * needed by the sdt. Ex. Key=Value specified in the output file, where, Key=the attribute value
   * found in the output file and Value=the old value which is needed by the sdt.
   */
  private HashMap nameMap = new HashMap();

  /** Creates a production XmlDataOutputParser instance. */
  public XmlDataOutputParser() {
    this(false);
  }

  /**
   * Initializes the instance of the XmlDataOutputParser for testing or production mode.
   * 
   * @param isTesting Whether the instance will be used for testing or production.
   */
  public XmlDataOutputParser(boolean isTesting) {
    this.isTesting = isTesting;
    for (Iterator i = XmlDataController.getObservers(); i.hasNext();) {
      Observer observer = (Observer) i.next();
      this.addObserver(observer);
    }
  }

  /**
   * Processes an XML data file to extract sensor data entries, which are then sent to the shell.
   * 
   * @param fileNameString The XML file name to be processed.
   * @param runtime The runtime of the XML data sensor.
   * @return The number of entries that have been processed in this XML file.
   * @exception XmlSensorException If an error occurs during the opening or parsing of the XML file.
   */
  public int processXmlDataFile (String fileNameString, Date runtime) throws XmlSensorException {
    try {
      // Build the XML document object using JDOM.
      SAXBuilder builder = new SAXBuilder();
      File xmlFile = new File(fileNameString);
      Document document = builder.build(xmlFile);
      this.changed("Processing file: " + fileNameString);
      return this.processXmlDataFromDocument(document, runtime);
    }
    catch (IOException e) {
      throw new XmlSensorException("Failed to open XML file: " + fileNameString);
    }
    catch (JDOMException e) {
      throw new XmlSensorException("Failed to build JDOM document for XML file: " + fileNameString);
    }
  }

  /**
   * Processes a JDOM document to extract sensor data entries, which are then sent to the shell.
   * 
   * @param document The document from which sensor data entries are extracted.
   * @param runtime The runtime of the XML data sensor.
   * @return The number of entries processed from the document.
   */
  public int processXmlDataFromDocument (Document document, Date runtime) {
    Element xmlSensorDataRoot = document.getRootElement();
    List entryList = xmlSensorDataRoot.getChildren();
    int entryCount = 1;
    int validEntries = 0;
    String toolName = null;

    // parses through each entry in the file
    for (Iterator i = entryList.iterator(); i.hasNext();) {
      String currentSdtName = this.sdtName;
      Element entryElement = (Element) i.next();
      List attributes = entryElement.getAttributes();
      String tstampString = (String) entryElement.getAttributeValue("tstamp");
      boolean hasTstamp = false;
      long tstamp = 0;
      if (tstampString != null) { // Gets the tstamp value if it is provided.
        hasTstamp = true;
        tstamp = Long.parseLong(tstampString);
      }
      HashMap attributeMap = new HashMap();
      // adds attributes to the attributeMap
      for (Iterator j = attributes.iterator(); j.hasNext();) {
        Attribute attribute = (Attribute) j.next();
        String attributeName = attribute.getName();
        String attributeValue = attribute.getValue();
        attributeName = this.getSdtRequiredAttributeName(attributeName);
        if ("tool".equals(attributeName)) {
          toolName = attributeValue; // adds toolName
        }
        else if ("sdt".equals(attributeName)) {
          currentSdtName = attributeValue;
        }
        attributeMap.put(attributeName, attributeValue);
      }
      if (currentSdtName == null && this.sdtName == null) {
        XmlDataController.notifyCommandListeners(new CommandEvent("Error: the sdt "
            + "attribute must be " + "associated with element " + entryCount
            + ".  Data will not be sent from the element.", false, false));
      }
      else if (toolName == null) {
        XmlDataController.notifyCommandListeners(new CommandEvent("Error: the tool "
            + "attribute must be " + "associated with element " + entryCount
            + ".  Data will not be sent from the element.", false, false));
      }
      else {
        this.setRequiredSdtAttributes(currentSdtName);
        List argList = new ArrayList();
        List entryAttributes = new ArrayList();
        SensorDataPropertyMap pMap = new SensorDataPropertyMap();
        argList.add("add");

        // populates the arglist needed by sensorshell
        for (Iterator j = attributeMap.keySet().iterator(); j.hasNext();) {
          String attributeName = (String) j.next();
          String attributeValue = (String) attributeMap.get(attributeName);
          // adds required fields
          if (this.isRequiredAttribute(attributeName) && !"tstamp".equals(attributeName)) {
            argList.add(attributeName + "=" + attributeValue);
            entryAttributes.add(attributeName);
          }
          // add fields that are not sdt, pMap, tstamp to pMap
          else if (!attributeName.equals("sdt") && !"tstamp".equals(attributeName)) {
            pMap.put(attributeName, attributeValue);
          }
        }
        
        // If createRunTime enabled, then add that attribute.
        if (this.createRunTime) {
          // Add it as a required field if it's a required field.
          if (this.isRequiredAttribute(this.runTimeName)) {
            argList.add(this.runTimeName + "=" + this.runTimeValue);
            entryAttributes.add(this.runTimeName);
          }
          // Otherwise add it to the plist.
          else {
            pMap.put(this.runTimeName, this.runTimeValue);
          }
        }

        // add the PMap to the arglist if there are any optional fields
        Set pMapKeys = pMap.keySet();
        if (pMapKeys.size() > 0) {
          argList.add("pMap=" + pMap.encode());
        }
        
        this.setupSensorShell(toolName, currentSdtName);

        // Only send it to the shell if all required fields are present.
        if (this.hasAllRequiredAttributes(entryAttributes, entryCount)) {
          Date theTstamp = (hasTstamp ? new Date(tstamp) : new Date());
          this.shell.doCommand(theTstamp, currentSdtName, argList);
          this.changed("Sending to sensorshell: " + currentSdtName + " " + argList);
          validEntries++;
        }
      }
      entryCount++; // current element
    }
    if (validEntries > 0) {
      this.shells.add(this.shell); // adds shell to list
    }
    // No need to create a new sensorshell for each type of entry.
    //this.shell = null; 
    return validEntries;
  }

  /**
   * Sets up the SensorShell to be used for this run.  The log file associated with this
   * run will be called XmlData, and all of the entries (even if from multiple tools and of 
   * multiple SDTs) will be sent in a single shell.  
   * 
   * @param toolName The name of the tool. (Ex. JUnit, Eclipse, Locc, etc).
   * @param sdtName The name of the SensorDataType. (Ex. FileMetric, Issue, Activity, etc).
   */
  private void setupSensorShell (String toolName, String sdtName) {
    if (this.shell == null) {
      SensorProperties sensorProps = null;
      // if running unit tests
      if (this.isTesting) {
        sensorProps = new SensorProperties(ServerProperties.getInstance().getHackystatHost(),
                                           UserManager.getInstance().getTestUser().getUserKey());
      }
      // else set the tool and type of sensor data.
      else {
        sensorProps = new SensorProperties(toolName);
      }
      this.shell = new SensorShell(sensorProps, false, "XmlData");
    }
  }

  /**
   * Returns true if the element in the xml file has all of the required attributes specified by the
   * sensor data type.
   * 
   * @param entryAttributes The list of attributes found in the xml file.
   * @param entryCount the element in the xml file that this class is currently evaluating.
   * @return True if the xml file has all of the required attributes. False if not.
   */
  private boolean hasAllRequiredAttributes (List entryAttributes, int entryCount) {
    for (Iterator i = this.sdtRequiredAttributes.iterator(); i.hasNext();) {
      String requiredAttribute = (String) i.next();
      if (!entryAttributes.contains(requiredAttribute) && !requiredAttribute.equals("pMap")
          && !requiredAttribute.equals("tstamp")) {
        String message = "Error: The " + requiredAttribute
            + " attribute is required, but does not exist. " + "Data from element " + entryCount
            + " will not be sent.";
        XmlDataController.notifyCommandListeners(new CommandEvent(message, false, false));
        return false;
      }
    }
    return true;
  }

  /**
   * Returns a list of all the required attributes specified by the <code>SensorDataType</code>.
   * 
   * @param sdtName the sensor data type name.
   * @throws NullPointerException thrown if the sensor data type specified does not exist in the
   *         <code>SdtManager</code>.
   */
  private void setRequiredSdtAttributes (String sdtName) throws NullPointerException {
    this.sdtRequiredAttributes.clear();
    SensorDataType sensorDataType = SdtManager.getInstance().getSdt(sdtName);
    if (sensorDataType == null) {
      throw new NullPointerException("Error: The sdt specified (" + sdtName + ") does not exist.");
    }
    for (Iterator i = sensorDataType.getAttributeIterator(); i.hasNext();) {
      EntryAttribute entryAttribute = (EntryAttribute) i.next();
      this.sdtRequiredAttributes.add(entryAttribute.getName());
      sensorDataType.getAttribute(entryAttribute.getName());
    }
  }

  /**
   * Returns if the attribute name is required by the SensorDataType.
   * 
   * @param attributeName the name of the attribute.
   * @return true if the attribute is required, false if not.
   */
  private boolean isRequiredAttribute (String attributeName) {
    return this.sdtRequiredAttributes.contains(attributeName);
  }

  /**
   * Returns the iterator over a list of <code>SensorShell</code> objects that contain metric
   * data.
   * 
   * @return the iterator over a list of <code>SensorShell</code> objects.
   */
  public Iterator getShells () {
    return this.shells.iterator();
  }

  /**
   * Sets if the XmlDataSensor is being tested.
   * 
   * @param isTesting true if the sensor is being tested, false if not.
   */
  public void setTesting (boolean isTesting) {
    this.isTesting = isTesting;
  }

  /**
   * Specifies that the runTime attribute should be added, and what the values should be.
   * 
   * @param runTimeName The name of the runTime attribute.
   * @param runTimeValue The runTime value (a long as a string).
   */
  public void setRunTime (String runTimeName, String runTimeValue) {
    this.createRunTime = true;
    this.runTimeName = runTimeName;
    this.runTimeValue = runTimeValue;
  }

  /**
   * Sets the name of the sensor data type that the output file is associated with.
   * 
   * @param sdtName the sensor data type name.
   */
  public void setSdtName (String sdtName) {
    this.sdtName = sdtName;
  }

  /**
   * Returns the attribute name required by the sensor data type. The required name is mapped to the
   * name specified in the output file.
   * 
   * @param attributeName the attribute name in the output file.
   * @return the valid sdt attribute name.
   */
  private String getSdtRequiredAttributeName (String attributeName) {
    if (this.nameMap.containsKey(attributeName)) {
      return (String) this.nameMap.get(attributeName);
    }
    else {
      // value not found in map, return it.
      return attributeName;
    }
  }

  /**
   * Sets the nameMap
   * 
   * @param nameMap the nameMap.
   */
  public void setNameMap (HashMap nameMap) {
    this.nameMap = nameMap;
  }

  /**
   * Notifies all the observers of this class of the change that has occured. Observers are only
   * notified if verbose mode is on.
   * 
   * @param message the message that describes the change.
   */
  public void changed (String message) {
    if (XmlDataController.isVerbose()) {
      this.setChanged();
      this.notifyObservers(message);
    }
  }
}
