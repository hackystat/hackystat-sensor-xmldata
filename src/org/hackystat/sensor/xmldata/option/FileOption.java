package org.hackystat.sensor.xmldata.option;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.hackystat.sensor.xmldata.XmlDataController;
import org.hackystat.sensor.xmldata.jaxb.Entries;
import org.hackystat.sensor.xmldata.jaxb.Entry;
import org.hackystat.sensor.xmldata.jaxb.XmlData;
import org.hackystat.sensorshell.SensorProperties;
import org.hackystat.sensorshell.SensorShell;
import org.hackystat.utilities.tstamp.Tstamp;
import org.hackystat.utilities.tstamp.TstampSet;
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
   * files, and sending them to the sensorbase.
   */
  public void execute() {
    SensorProperties properties = new SensorProperties();
    SensorShell shell = new SensorShell(properties, false, "XmlData", true);

    // Do not execute if the host cannot be reached. This check will exist until
    // offline data storage is implemented.
    if (!shell.ping()) {
      String msg = "The host, " + this.getController().getHost()
          + ", could not be reached.  No data will be sent.";
      this.getController().fireMessage(msg);
      return;
    }

    try {
      // Create one runtime for the entire batch of files.
      XMLGregorianCalendar runTime = Tstamp.makeTimestamp();

      for (String filePath : this.getParameters()) {
        this.getController().fireVerboseMessage("Sending data from: " + filePath);
        // First, let's unmarshall the current file.
        File file = new File(filePath);
        JAXBContext context = JAXBContext
            .newInstance(org.hackystat.sensor.xmldata.jaxb.ObjectFactory.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        // Adds schema validation to the unmarshalled file.
        SchemaFactory schemaFactory = SchemaFactory
            .newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = schemaFactory.newSchema(new File("xml/schema/xmldata.xsd"));
        unmarshaller.setSchema(schema);

        XmlData xmlData = (XmlData) unmarshaller.unmarshal(file);
        Entries entries = xmlData.getEntries();
        // Only send data if the sdt is set or all entries have sdt attributes.
        TstampSet tstampSet = new TstampSet();
        Object sdtName = this.getController().getOptionObject(Options.SDT);
        if (sdtName != null || this.hasSdtAttributes(entries.getEntry())) {
          for (Entry entry : entries.getEntry()) {
            // Then, lets set the required attributes.
            Map<String, String> keyValMap = new HashMap<String, String>();
            keyValMap.put("Tool", entry.getTool());
            keyValMap.put("Resource", entry.getResource());
            keyValMap.put("SensorDataType", (String) sdtName);

            // Creates a unique timestamp for each entry.
            long uniqueTstamp = tstampSet.getUniqueTstamp(file.lastModified());
            XMLGregorianCalendar gregorianTime = this.convertLongToGregorian(uniqueTstamp);
            keyValMap.put("Timestamp", gregorianTime.toString());
            keyValMap.put("Runtime", runTime.toString());

            // Next, add the optional attributes.
            Map<QName, String> map = entry.getOtherAttributes();
            for (Map.Entry<QName, String> attributeEntry : map.entrySet()) {
              // TODO: when the format for timestamps is resolved,
              // uniqueTimestamps can be handled.
              // If entries contain tstamps, and the unique flag is set.
              // if ("Timestamp".equals(attributeEntry.getKey().toString())
              // && Boolean.TRUE.equals(this.getController().getOptionObject(
              // Options.UNIQUE_TSTAMP))) {
              // Timestamp stamp = Timestamp.valueOf(attributeEntry.getValue());
              // gregorianTime = this
              // .convertLongToGregorian(new Long(attributeEntry.getValue()));
              // System.out.println(Tstamp.makeTimestamp(attributeEntry.getValue()));
              // keyValMap.put(attributeEntry.getKey().toString(),
              // gregorianTime.toString());
              // }
              // else {
              keyValMap.put(attributeEntry.getKey().toString(), attributeEntry.getValue());
              // }
            }
            // Finally, add the mapping and send the data.
            this.getController().fireVerboseMessage(this.getMapVerboseString(keyValMap));
            shell.add(keyValMap);
          }
        }
        else {
          String msg = "The -sdt flag must be specified for all entries or each "
              + "xml entry must have the 'SensorDataType' attribute.";
          throw new Exception(msg);
        }
      }
      this.getController().fireMessage(
          shell.send() + " entries sent to " + this.getController().getHost());
      shell.quit();
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

  /**
   * Returns the string containing the information stored in the key-value
   * mapping of sensor data. This string is helpful when running this option in
   * verbose mode.
   * @param keyValMap the map used to generate the returned string.
   * @return the informative string.
   */
  private String getMapVerboseString(Map<String, String> keyValMap) {
    if (!keyValMap.isEmpty()) {
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

  /**
   * Converts a time represented in a long to an
   * <code>XmlGregorianCalendar</code>. Taken from the ant sensor module.
   * 
   * @param timeInMillis The time to convert in milliseconds.
   * @return Returns the time passed in as a <code>XmlGregorianCalendar</code>.
   */
  private XMLGregorianCalendar convertLongToGregorian(long timeInMillis) {
    // convert long time into calendar object
    GregorianCalendar cal = new GregorianCalendar();
    cal.setTimeInMillis(timeInMillis);

    // get an instance of XMLGregorianCalendar from Tstamp
    XMLGregorianCalendar xmlCalendar = Tstamp.makeTimestamp();
    // modify the instance with the values from the Calendar
    xmlCalendar.setMonth(cal.get(Calendar.MONTH));
    xmlCalendar.setDay(cal.get(Calendar.DAY_OF_MONTH));
    xmlCalendar.setYear(cal.get(Calendar.YEAR));
    xmlCalendar.setHour(cal.get(Calendar.HOUR_OF_DAY));
    xmlCalendar.setMinute(cal.get(Calendar.MINUTE));
    xmlCalendar.setSecond(cal.get(Calendar.SECOND));
    xmlCalendar.setMillisecond(cal.get(Calendar.MILLISECOND));
    return xmlCalendar;
  }
}
