package org.hackystat.sensor.xmldata.option;

import java.util.ArrayList;
import java.util.List;

import org.hackystat.sensor.xmldata.XmlDataController;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests if the SdtOption operates as intended.
 * @author aito
 * 
 */
public class SdtOptionTest {
  /**
   * Tests if isValid returns the correct value depending on the specified
   * parameters.
   */
  @Test
  public void testIsValid() {
    XmlDataController controller = new XmlDataController(new ArrayList<String>());
    // Tests a valid sdt option.
    List<String> arguments = new ArrayList<String>();
    arguments.add("DevEvent");
    Option sdtOption = SdtOption.createSdtOption(controller, arguments);
    Assert.assertTrue("SdtOptions accept only 1 argument.", sdtOption.isValid());

    // Tests an invalid sdt option.
    sdtOption = SdtOption.createSdtOption(controller, new ArrayList<String>());
    Assert.assertFalse("SdtOptions must have 1 argument.", sdtOption.isValid());
  }
}
