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
public class FileOption extends AbstractOption implements Executable {
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
      String msg = "The number of parameters must include at least 1 file. Ex: -file foo.xml foo2.xml";
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
   * files, and sending them to the sensorbase.
   */
  public void execute() {
    SensorProperties properties = new SensorProperties();
    SensorShell shell = new SensorShell(properties, false, "XmlData");
    int entryCount = 0;
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
        for (Entry entry : entries.getEntry()) {
          // Then, lets set the required attributes.
          Map<String, String> keyValMap = new HashMap<String, String>();
          keyValMap.put("Tool", entry.getTool());
          keyValMap.put("Resource", entry.getResource());
          keyValMap.put("SensorDataType", this.getController().getSdtName());
          keyValMap.put("Runtime", Tstamp.makeTimestamp().toString());

          // Next, add the optional attributes.
          Map<QName, String> map = entry.getOtherAttributes();
          for (Map.Entry<QName, String> attributeEntry : map.entrySet()) {
            keyValMap.put(attributeEntry.getKey().toString(), attributeEntry.getValue());

          }
          // Finally, add the mapping and send the data.
          shell.add(keyValMap);
          entryCount += shell.send();
        }
      }
      shell.quit();
      this.getController().fireMessage(
          entryCount + " entries sent to " + this.getController().getHost());
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
  }
}
