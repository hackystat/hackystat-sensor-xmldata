package org.hackystat.sensor.xmldata.option;

import java.util.List;

import org.hackystat.sensorshell.SensorShell;

/**
 * Implements the "-sdt" option. Sets the sdt to be what the user specifies, but does not override
 * the sdt definition in output files.
 * 
 * @author Austen Ito
 * 
 */
public class SdtOption extends  AbstractOption {
  public static final String OPTION_NAME = "-sdt";
  
  private SdtOption(String name, List<String> parameters){
    super(name, parameters);
  }
  
  public static Option createSdtOption(String name, List<String> parameters){
    Option option = new SdtOption(name, parameters);
    return option;
  }
  
  @Override
  public boolean isValid() {
    return true;
  }

  public void execute(SensorShell shell) {
    // TODO Auto-generated method stub
    
  }
}
