package org.hackystat.sensor.xmldata.option;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.hackystat.sensor.xmldata.XmlDataController;
import org.hackystat.sensor.xmldata.devevent.jaxb.Entries;
import org.hackystat.sensor.xmldata.devevent.jaxb.Entry;
import org.hackystat.sensor.xmldata.devevent.jaxb.XmlData;
import org.hackystat.sensorbase.resource.sensordata.Tstamp;
import org.hackystat.sensorshell.SensorProperties;
import org.hackystat.sensorshell.SensorShell;
import org.xml.sax.SAXException;

/**
 * The option used to send generic sensor data, via the sensorshell, to the
 * sensorbase. This option accepts a list of files that contain the generic
 * sensor information.
 * @author aito
 * 
 */
public class FileOption extends AbstractOption {
  /** The name of this option, which is "-file". */
  public static final String OPTION_NAME = "-file";

  /**
   * Private constructor that creates this option with the specified controller,
   * name, and parameters.
   * @param controller the specified controller.
   * @param name the specified name.
   * @param parameters the specified parameters.
   */
  private FileOption(XmlDataController controller, String name, List<String> parameters) {
    super(controller, name, parameters);
  }

  /**
   * Constructs this option with the specified controller and parameters.
   * "-file" is used as the name of this option.
   * @param controller the specified controller.
   * @param parameters the specified parameters.
   * @return the option instance.
   */
  public static Option createOption(XmlDataController controller, List<String> parameters) {
    Option option = new FileOption(controller, OPTION_NAME, parameters);
    return option;
  }

  /**
   * Returns true if the the list of parameters contains 1 or more files that
   * exist.
   * @return true if this option's parameters are valid.
   */
  @Override
  public boolean isValid() {
    if (this.getParameters().size() == 0) {
      String msg = "The number of parameters must include at least 1 file. "
          + "Ex: -file foo.xml foo2.xml";
      this.getController().fireMessage(msg);
      return false;
    }

    for (String parameter : this.getParameters()) {
      File file = new File(parameter);
      if (!file.exists()) {
        String msg = "The file '" + file + "' does not exist.";
        this.getController().fireMessage(msg);
        return false;
      }
    }
    return true;
  }

  /**
   * Executes this option by grabbing all information stored in the specified
   * files, and sending them to the sensorbase. Note that this method will send
   * data after the data from each file is retrieved. This means that if file
   * foo.xml has valid data and file foo2.xml does not, foo.xml's data will be
   * sent and then this method will fail on foo2.xml's data.
   */
  public void execute() {
    SensorProperties properties = new SensorProperties();
    SensorShell shell = new SensorShell(properties, false, "XmlData", true);
    int entryCount = 0;

    // Do not execute if the host cannot be reached. This check will exist until
    // offline data storage is implemented.
    if (!shell.ping()) {
      String msg = "The host, " + this.getController().getHost()
          + ", could not be reached.  No data will be sent.";
      this.getController().fireMessage(msg);
      return;
    }

    try {
      for (String filePath : this.getParameters()) {
        this.getController().fireVerboseMessage("Sending data from: " + filePath);
        // First, let's unmarshall the current file.
        File file = new File(filePath);
        JAXBContext context = JAXBContext
            .newInstance("org.hackystat.sensor.xmldata.devevent.jaxb");
        Unmarshaller unmarshaller = context.createUnmarshaller();

        // Adds schema validation to the unmarshelled file.
        SchemaFactory schemaFactory = SchemaFactory
            .newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = schemaFactory.newSchema(new File("xml/schema/xmldata.xsd"));
        unmarshaller.setSchema(schema);

        XmlData xmlData = (XmlData) unmarshaller.unmarshal(file);
        Entries entries = xmlData.getEntries();
        // Only send data if the sdt is set or all entries have sdt attributes.
        Object sdtName = this.getController().getOptionObject(Options.SDT);
        if (!"".equals(sdtName) || this.hasSdtAttributes(entries.getEntry())) {
          for (Entry entry : entries.getEntry()) {
            // Then, lets set the required attributes.
            Map<String, String> keyValMap = new HashMap<String, String>();
            keyValMap.put("Tool", entry.getTool());
            keyValMap.put("Resource", entry.getResource());
            keyValMap.put("SensorDataType", (String) sdtName);
            keyValMap.put("Runtime", Tstamp.makeTimestamp().toString());

            // Next, add the optional attributes.
            Map<QName, String> map = entry.getOtherAttributes();
            for (Map.Entry<QName, String> attributeEntry : map.entrySet()) {
              keyValMap.put(attributeEntry.getKey().toString(), attributeEntry.getValue());
            }
            // Finally, add the mapping and send the data.
            this.getController().fireVerboseMessage(this.getMapVerboseString(keyValMap));
            shell.add(keyValMap);
            entryCount += shell.send();
          }
        }
        else {
          String msg = "The -sdt flag must be specified for all entries or each "
              + "xml entry must have the 'SensorDataType' attribute.";
          throw new Exception(msg);
        }
      }
    }
    catch (JAXBException e) {
      String msg = "There was a problem unmarshalling the data.  File(s) "
          + "may not conform to the xmldata schema.";
      this.getController().fireMessage(msg, e.toString());
    }
    catch (SAXException e) {
      String msg = "The specified file(s) could not be parsed.";
      this.getController().fireMessage(msg, e.toString());
    }
    catch (Exception e) {
      String msg = "The specified file(s) failed to load.";
      this.getController().fireMessage(msg, e.toString());
    }
    finally {
      shell.quit();
      this.getController().fireMessage(
          entryCount + " entries sent to " + this.getController().getHost());
    }
  }

  /**
   * Returns the string containing the information stored in the key-value
   * mapping of sensor data. This string is helpful when running this option in
   * verbose mode.
   * @param keyValMap the map used to generate the returned string.
   * @return the informative string.
   */
  private String getMapVerboseString(Map<String, String> keyValMap) {
    if (keyValMap.size() > 0) {
      String verboseString = "[";
      for (Map.Entry<String, String> entry : keyValMap.entrySet()) {
        verboseString = verboseString.concat(entry.getKey() + "=" + entry.getValue()) + ", ";
      }

      // Remove the last ', ' from the string.
      verboseString = verboseString.substring(0, verboseString.length() - 2);
      return verboseString.concat("]");
    }
    return "";
  }

  /**
   * Returns true if the list of JAXB Entry object's each contain the sensor
   * data type attribute.
   * @param entries the list of entries to search.
   * @return true if each entry has the sdt attribute, false if not.
   */
  private boolean hasSdtAttributes(List<Entry> entries) {
    for (Entry entry : entries) {
      if (!this.hasSdtInAttributes(entry.getOtherAttributes())) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns true if the specified map contains the sensor data type attribute.
   * If the mapping does not have the sdt attribute, false is returned.
   * @param attributeMap the map to search.
   * @return true if the map has the sdt attribute, false if not.
   */
  private boolean hasSdtInAttributes(Map<QName, String> attributeMap) {
    for (QName name : attributeMap.keySet()) {
      if ("SensorDataType".equals(name.toString())) {
        return true;
      }
    }
    return false;
  }
}
