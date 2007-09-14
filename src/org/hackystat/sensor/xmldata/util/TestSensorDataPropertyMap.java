package org.hackystat.sensor.xmldata.util;

import junit.framework.TestCase;

/**
 * Tests the SensorDataPropertyMap implementation.
 * @author Philip M. Johnson
 * @version $Id: TestSensorDataPropertyMap.java,v 1.1.1.1 2005/10/20 23:56:44
 * johnson Exp $
 */
public class TestSensorDataPropertyMap extends TestCase {
  private static final String VALUE2 = "value2";
  /**
   * Tests the thread safe property map ADT.
   * @throws Exception if problems occur.
   */
  public void testThreadSafePropertyMap() throws Exception {
    // test normal creation, adding, getting.
    SensorDataPropertyMap map = new SensorDataPropertyMap();
    map.put("name1", "value1");
    assertEquals("testing retrieve 1", "value1", map.get("name1"));
    map.put("name2", VALUE2);
    assertEquals("testing retrieve 2", VALUE2, map.get("name2"));
    String encoding = map.encode();
    SensorDataPropertyMap map2 = new SensorDataPropertyMap(encoding);
    assertEquals("testing retrieve 3", "value1", map2.get("name1"));
    assertEquals("testing retrieve 4", VALUE2, map2.get("name2"));
    assertEquals("testing retrieve 5", null, map2.get("name3"));
    assertEquals("testing retrieve 5", null, map2.get("name3"));
    assertEquals("testing retrieve 6", "foo", map2.get("name3", "foo"));
    assertEquals("testing retrieve 7", VALUE2, map2.getIgnoreCase("Name2"));
    // Test to see that an encoded empty map is OK.
    SensorDataPropertyMap map3 = new SensorDataPropertyMap();
    String emptyEncoding = map3.encode();
    new SensorDataPropertyMap(emptyEncoding);
  }
}
