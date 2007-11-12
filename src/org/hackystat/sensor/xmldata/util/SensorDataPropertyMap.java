package org.hackystat.sensor.xmldata.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides a thread-safe property map implementation for use as the value of
 * the default 'pMap' field in all Sensor Data. Features of this abstract data
 * type include:
 * <ul>
 * <li> Accepts only Strings as keys and values.
 * <li> The entire property map can be encoded into a string for SOAP
 * transmission.
 * <li> The encoded string can be provided to a constructor to rebuild the
 * object.
 * <li> The puts and gets are thread-safe.
 * </ul>
 * @author Philip M. Johnson
 * @version $Id: SensorDataPropertyMap.java,v 1.1.1.1 2005/10/20 23:56:44
 * johnson Exp $
 */
public class SensorDataPropertyMap {

  /** A thread-safe map holding the string property names and values. */
  private ConcurrentHashMap<String, String> propertyMap = new ConcurrentHashMap<String, String>();

  /**
   * The default public constructor for the SensorDataPropertyMap.
   */
  public SensorDataPropertyMap() {
    // do nothing.
  }

  /** The default empty SensorDataPropertyMap String representation. * */
  private static String defaultMapString = new SensorDataPropertyMap().encode();

  /**
   * Creates a thread-safe plist, initializing it with the contents of
   * encodedMap.
   * @param encodedMap A string produced from the encode() method.
   * @throws Exception If encodedMap is not a legal encoded
   * SensorDataPropertyMap.
   */
  public SensorDataPropertyMap(String encodedMap) throws Exception {
    try {
      List<String> propertyList = StringListCodec.decode(encodedMap);
      for (Iterator<String> i = propertyList.iterator(); i.hasNext();) {
        String propertyName = i.next();
        String propertyValue = i.next();
        propertyMap.put(propertyName, propertyValue);
      }
    }
    catch (Exception e) {
      throw new Exception("Error constructing SensorDataPropertyMap", e);
    }
  }

  /**
   * Puts the (name, value) pair into the SensorDataPropertyMap.
   * @param name The property name string.
   * @param value The property value string.
   */
  public void put(String name, String value) {
    this.propertyMap.put(name, value);
  }

  /**
   * Gets the property value associated with name, or returns null if not found.
   * @param name The property name whose value is to be retrieved (if
   * available).
   * @return The property value associated with name, or null if not found.
   */
  public String get(String name) {
    return this.propertyMap.get(name);
  }

  /**
   * Returns the property value associated with name, where name is not
   * case-sensitive.
   * @param name The case-insensitive property name whose value is to be
   * retrieved.
   * @return The property value, or null if no case-insensitive key (name) is
   * found.
   */
  public String getIgnoreCase(String name) {
    // Property maps should not have large numbers of elements, so iteration
    // should be OK.
    for (String key : this.propertyMap.keySet()) {
      if (key.equalsIgnoreCase(name)) {
        return this.propertyMap.get(key);
      }
    }
    return null;
  }

  /**
   * Returns a string containing the "runTime" attribute, or null if not found.
   * Since there has been confusion involving the spelling of this attribute,
   * this method tries both "runTime" and "runtime". If both attributes are
   * present (almost surely an error), the value of the runTime version is
   * returned.
   * @return A string containing the runTime (or runtime) attribute.
   */
  public String getRunTime() {
    String runTime = this.propertyMap.get("runTime");
    if (runTime == null) {
      runTime = this.propertyMap.get("runtime");
    }
    return runTime;
  }

  /**
   * Returns the property value associated with name, or defaultValue if not
   * found.
   * @param name The property name whose value is to be retrieved.
   * @param defaultValue The defaultValue to be returned if not present.
   * @return The property value associated with name, or defaultValue if not
   * found.
   */
  public String get(String name, String defaultValue) {
    String value = get(name);
    return (value == null) ? defaultValue : value;
  }

  /**
   * Encodes the contents of this PropertyMap into a String that can be
   * persisted or transmitted using Soap. Throws a RuntimeException if an error
   * occurs during encoding, which should be extremely rare.
   * @return An encoded string.
   */
  public String encode() {
    ArrayList<String> propertyList = new ArrayList<String>(this.propertyMap.size() * 2);
    for (String name : this.propertyMap.keySet()) {
      String value = this.propertyMap.get(name);
      propertyList.add(name);
      propertyList.add(value);
    }
    String encodedString;
    try {
      encodedString = StringListCodec.encode(propertyList);
    }
    catch (Exception e) {
      throw new RuntimeException("Problems encoding the property map string", e);
    }
    return encodedString;
  }

  /**
   * Returns the contents of the property map in its encoded form. This is
   * required for sensor data transmission to occur correctly.
   * @return The contents of the property map in its encoded form.
   */
  public String toString() {
    // return this.propertyMap.toString();
    return this.encode();
  }

  /**
   * Returns the contents of the property map in human readable form.
   * @return The property map in human readable form.
   */
  public String formattedString() {
    return this.propertyMap.toString();
  }

  /**
   * Returns a set containing the keys associated with this property map.
   * @return The keyset.
   */
  public Set<String> keySet() {
    return this.propertyMap.keySet();
  }

  /**
   * Returns a new SensorDataPropertyMap instance generated from the encoded
   * String. This method supplied for use as the Converter method in the SDT
   * definition.
   * @param encodedMap The encoded version of a SensorDataPropertyMap.
   * @return A new SensorDataPropertyMap instance.
   * @throws Exception If problems occur decoding the encoded string.
   */
  public static SensorDataPropertyMap getMap(String encodedMap) throws Exception {
    return new SensorDataPropertyMap(encodedMap);
  }

  /**
   * Returns a new String representation of an empty SensorDataPropertyMap
   * instance. This method supplied for use as the Defaulter method in the SDT
   * definition.
   * @return A new String encoding of an empty SensorDataPropertyMap instance.
   */
  public static String getDefaultMapString() {
    return defaultMapString;
  }

}
