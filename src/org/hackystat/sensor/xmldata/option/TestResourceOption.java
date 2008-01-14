package org.hackystat.sensor.xmldata.option;

import java.util.ArrayList;
import java.util.List;

import org.hackystat.sensor.xmldata.XmlDataController;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests if the resource option accepts and processes the correct arguments.
 * @author aito
 * 
 */
public class TestResourceOption {
  /**
   * Tests if isValid returns the correct value depending on the specified
   * parameters.
   */
  @Test
  public void testIsValid() {
    XmlDataController controller = new XmlDataController();
    // Tests a valid sdt option.
    List<String> arguments = new ArrayList<String>();
    arguments.add("C:\\Foo.java");
    Option resourceOption = OptionFactory.getInstance(controller, ResourceOption.OPTION_NAME,
        arguments);
    Assert.assertTrue("Resource Options accept only 1 argument.", resourceOption.isValid());

    // Tests an invalid sdt option.
    resourceOption = new ResourceOption(controller, new ArrayList<String>());
    Assert.assertFalse("Resource Options must have 1 argument.", resourceOption.isValid());
  }
}
