package org.hackystat.sensor.xmldata.option;

/**
 * The enumeration of options that have can have associated objects. Each option
 * can be mapped to other objects during the processing phase of options and be
 * retrieved by using each option in this class as a key.
 * @author aito
 * 
 */
public enum Options {
  /**
   * The option set when running in verbose mode. The object associated with
   * this option is a boolean.
   */
  VERBOSE,
  /**
   * The option set when specifying the SensorDataType. The object associated
   * with this option is a String.
   */
  SDT,
  /**
   * The option set when specifying unique timestamps found in the sensor data
   * file. The object associated with this option is a boolean.
   */
  UNIQUE_TSTAMP,
  /**
   * The option set when the user requests to send data using muliple
   * sensorshell instances. This allows speedier data sending.
   */
  MULTI_SHELL,
  /**
   * The option set when the user requests to send data as a batch, which means
   * that all data instances have the same runtime.
   */
  SET_RUNTIME,
  /**
   * The option set when all entries sent by this sensor belongs to the same
   * specified resource.
   */
  RESOURCE;
}
