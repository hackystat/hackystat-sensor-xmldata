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
import org.hackystat.sensor.xmldata.jaxb.v7.Entry;
import org.hackystat.sensor.xmldata.jaxb.v7.Sensor;
import org.hackystat.sensorshell.SensorProperties;
import org.hackystat.sensorshell.SensorShell;
import org.hackystat.utilities.tstamp.Tstamp;
import org.hackystat.utilities.tstamp.TstampSet;
import org.xml.sax.SAXException;

/**
 * The option used to port Hackystat version 7 (v7) data to the Hackystat 8
 * sensorbase with the following steps:
 * 
 * <pre>
 * 1. The user provides the Hackystat 7 user directory, the version 7
 * account, the version 8 username, and version 8 password. 
 * 2. Then this option traverses the Hackystat 7 data directory and converts the information to
 * valid Hackystat 8 data.  
 * 3. Finally, the converted data is sent to the Hackystat 8 sensorbase.
 * </pre>
 * 
 * @author aito
 * 
 */
public class MigrationOption extends AbstractOption {
  /** The name of this option, which is "-migration". */
  public static final String OPTION_NAME = "-migration";
  /** The version 7 data directory specified to convert to version 8 data. */
  private File v7DataDir = null;
  /**
   * The sensor properties file containing the version 8 host and account
   * information.
   */
  private SensorProperties properties = new SensorProperties();

  /**
   * Private constructor that creates this option with the specified controller,
   * name, and parameters.
   * @param controller the specified controller.
   * @param name the specified name.
   * @param parameters the specified parameters.
   */
  private MigrationOption(XmlDataController controller, String name, List<String> parameters) {
    super(controller, name, parameters);
  }

  /**
   * Constructs this option with the specified controller and parameters.
   * "-migration" is used as the name of this option.
   * @param controller the specified controller.
   * @param parameters the specified parameters.
   * @return the option instance.
   */
  public static Option createOption(XmlDataController controller, List<String> parameters) {
    Option option = new MigrationOption(controller, OPTION_NAME, parameters);
    return option;
  }

  /**
   * Returns true if the specified option parameters follows the convention:
   * 
   * <pre>
   * [v7 directory] [v7 account] [v8 username] [v8 password]
   * 
   * Ex:  -migration C:\foo ABCDEF austen@hawaii.edu fooPassword
   * Note that the v7 directory does not include the v7 account name.
   * </pre>
   * 
   * @return true if the option parameters are correct, false if not.
   */
  @Override
  public boolean isValid() {
    // Check if the correct amount of parameters exist.
    if (this.getParameters().isEmpty() || this.getParameters().size() > 4) {
      String msg = "The -argList option only accepts four parameters, "
          + "[v7 directory] [v7 account] [v8 username] [v8 password]";
      this.getController().fireMessage(msg);
      return false;
    }

    // Verify that the version 7 directory exists.
    File v7Dir = new File(this.getParameters().get(0));
    String v7Account = this.getParameters().get(1);
    if (!new File(v7Dir.getAbsolutePath() + "/" + v7Account).exists()) {
      String msg = "The version 7 user directory, " + this.getParameters().get(0) + "/"
          + this.getParameters().get(1) + ", does not exist.";
      this.getController().fireMessage(msg);
      return false;
    }

    // Verify that the version 8 account and password is valid.
    this.properties = new SensorProperties(this.properties.getHackystatHost(), this
        .getParameters().get(2), this.getParameters().get(3));
    SensorShell shell = new SensorShell(this.properties, false, "XmlData", true);
    if (!shell.ping()) {
      String msg = "The connection or account information is incorrect: Hackystat Host="
          + this.properties.getHackystatHost() + ", v8Account=" + this.getParameters().get(2)
          + ", v8Password=" + this.getParameters().get(3);
      this.getController().fireMessage(msg);
      return false;
    }
    return true;
  }

  /** Sets the variables used by the execute method. */
  @Override
  public void process() {
    File v7Dir = new File(this.getParameters().get(0));
    String v7Account = this.getParameters().get(1);
    this.v7DataDir = new File(v7Dir.getAbsolutePath() + "/" + v7Account + "/data");

    this.properties = new SensorProperties(this.properties.getHackystatHost(), this
        .getParameters().get(2), this.getParameters().get(3));
  }

  /**
   * Executes this option by converting all version 7 data found in the
   * specified directory to version 8 compatiable data. The converted data is
   * sent to the Hackystat 8 sensorbase.
   */
  @Override
  public void execute() {
    SensorShell shell = new SensorShell(this.properties, false, "XmlData", true);
    TstampSet tstampSet = new TstampSet();

    // Iterates over each file in the version 7 data directory.
    for (File sdtDir : this.v7DataDir.listFiles()) {
      this.getController().fireVerboseMessage("Processing " + sdtDir);
      for (File sensorDataFile : sdtDir.listFiles()) {
        try {
          JAXBContext context = JAXBContext
              .newInstance(org.hackystat.sensor.xmldata.jaxb.v7.ObjectFactory.class);
          Unmarshaller unmarshaller = context.createUnmarshaller();

          // Adds schema validation to the unmarshalled file.
          SchemaFactory schemaFactory = SchemaFactory
              .newInstance("http://www.w3.org/2001/XMLSchema");
          Schema schema = schemaFactory.newSchema(new File("xml/schema/v7data.xsd"));
          unmarshaller.setSchema(schema);

          Sensor sensor = (Sensor) unmarshaller.unmarshal(sensorDataFile);
          for (Entry entry : sensor.getEntry()) {
            Map<String, String> keyValMap = new HashMap<String, String>();
            keyValMap.put("SensorDataType", sdtDir.getName());
            // Creates a unique timestamp for each entry.
            long uniqueTstamp = tstampSet.getUniqueTstamp(sensorDataFile.lastModified());
            keyValMap.put("Timestamp", Tstamp.makeTimestamp(uniqueTstamp).toString());

            // Add an entry for each key-value attribute in the data file.
            for (Map.Entry<QName, String> attribute : entry.getOtherAttributes().entrySet()) {
              this.addEntry(keyValMap, attribute, tstampSet);
              this.getController().fireVerboseMessage(
                  OptionUtil.getMapVerboseString(keyValMap));
              shell.add(keyValMap);
            }
          }
        }
        catch (JAXBException e) {
          String msg = "There was a problem unmarshalling the data.  The v7 data file(s) "
              + "may not conform to the xmldata schema.";
          this.getController().fireMessage(msg, e.toString());
        }
        catch (SAXException e) {
          String msg = "The v7 data file(s) could not be parsed.";
          this.getController().fireMessage(msg, e.toString());
        }
        catch (Exception e) {
          String msg = "The v7 data file(s) failed to load.";
          this.getController().fireMessage(msg, e.toString());
        }
      }
    }
    this.getController().fireMessage(
        shell.send() + " entries sent to " + this.getController().getHost());
    shell.quit();
  }

  /**
   * Adds the entry to the specified key-value mapping. This method performs
   * additional processing on the entry, such as converting the version 7
   * timestamp to a compatiable version 8 timestamp.
   * @param keyValMap the map reference that is populated by this method.
   * @param entry the entry, whose information is used to populate the specified
   * map.
   * @param tstampSet the set of timestamps used to generate a unique timestamp
   * for each entry.
   */
  private void addEntry(Map<String, String> keyValMap, Map.Entry<QName, String> entry,
      TstampSet tstampSet) {
    String entryName = entry.getKey().toString();
    String entryValue = entry.getValue();
    if ("tstamp".equalsIgnoreCase(entryName)) {
      entryName = "Timestamp";
      long timestamp = OptionUtil.getTimestampInMillis(entryValue);
      entryValue = Tstamp.makeTimestamp(timestamp).toString();

      // Create a unique tstamp if the option is set.
      if (Boolean.TRUE.equals(this.getController().getOptionObject((Options.UNIQUE_TSTAMP)))) {
        entryValue = Tstamp.makeTimestamp(tstampSet.getUniqueTstamp(timestamp)).toString();
      }
    }
    else if ("file".equalsIgnoreCase(entryName) || "path".equalsIgnoreCase(entryName)) {
      entryName = "Resource";
    }
    keyValMap.put(entryName, entryValue);
  }
}
