package org.hackystat.sensor.xmldata.util;

import junit.framework.TestCase;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * Tests the StringListCodec implementation.
 *
 * @author    Philip Johnson
 * @version   $Id: TestStringListCodec.java,v 1.1.1.1 2005/10/20 23:56:44 johnson Exp $
 */
public class TestStringListCodec extends TestCase {

  /**
   * Tests the stringlistcodec method.
   *
   * @exception Exception  if an error occurs
   */
  public void testStringListCodec() throws Exception {
    // Test simple.
    String[] stringList1 = {"abc", "defg"};
    List<String> originalList = Arrays.asList(stringList1);
    String encoded = StringListCodec.encode(originalList);
    ArrayList<String> decodedList = StringListCodec.decode(encoded);
    assertEquals("Testing simple list", originalList, decodedList);

    String[] stringList2 = {"abc", "defg", "", "hijklmnop"};
    List<String> originalList2 = Arrays.asList(stringList2);
    String encoded2 = StringListCodec.encode(Arrays.asList(stringList2));
    ArrayList<String> decodedList2 = StringListCodec.decode(encoded2);
    assertEquals("Testing empty list item", originalList2, decodedList2);

    ArrayList<String> emptyList = new ArrayList<String>();
    String encodedEmpty = StringListCodec.encode(emptyList);
    ArrayList decodedEmpty = StringListCodec.decode(encodedEmpty);
    assertEquals("Testing degenerate list", emptyList, decodedEmpty);
  }
  
  /**
   * Tests whether line breaks are coverted or not.
   * 
   * @throws Exception If test fails.
   */
  public void testLineBreak() throws Exception {
    ArrayList<String> original = new ArrayList<String>();
    original.add("\r\n00\r\n00\r\n");
    original.add("\r11\r11\r");
    original.add("\n22\n22\n");
    String encodedString = StringListCodec.encode(original);    

    ArrayList<String> decoded = StringListCodec.decode(encodedString);     
    assertEquals(original.size(), decoded.size());
    assertEquals("\n00\n00\n", decoded.get(0));
    assertEquals("\n11\n11\n", decoded.get(1));    
    assertEquals("\n22\n22\n", decoded.get(2));    
  }
}
