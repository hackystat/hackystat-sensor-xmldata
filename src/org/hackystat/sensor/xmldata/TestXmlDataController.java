package org.hackystat.sensor.xmldata;

import org.hackystat.sensor.xmldata.option.Options;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests if XmlDataController operates as intended.
 * @author aito
 * 
 */
public class TestXmlDataController {
  /**
   * Tests if the option map containing option value parameters stores and
   * retrieves the correct values.
   */
  @Test
  public void testAddAndGetOptionObject() {
    // Tests normal retrieval.
    XmlDataController controller = new XmlDataController();
    controller.addOptionObject(Options.UNIQUE_TSTAMP, Boolean.TRUE);
    Assert.assertEquals("The tstamp option returned an incorrect value.", Boolean.TRUE,
        controller.getOptionObject(Options.UNIQUE_TSTAMP));

    // Tests a null retrieval.
    Assert.assertEquals("The null option returned an incorrect value.", null, controller
        .getOptionObject(null));
  }
}
