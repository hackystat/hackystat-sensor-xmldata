package org.hackystat.sensor.xmldata.option;

import java.io.File;
import java.util.List;

import org.hackystat.sensor.xmldata.XmlDataController;

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
    return true;
  }

  /** Executes! */
  @Override
  public void execute() {
    // Currently no implementation.
  }
}
