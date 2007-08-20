package org.hackystat.sensor.xmldata.option;

import java.util.ArrayList;
import java.util.List;

import org.hackystat.sensor.xmldata.XmlDataController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests if the option handler stores, validates, and executes options as
 * intended.
 * @author aito
 * 
 */
public class TestOptionHandler {
  /** The handler class that is tested. */
  private OptionHandler handler = null;
  /** The controller instance. */
  private XmlDataController controller = null;

  /** Sets up each test case. */
  @Before
  public void setup() {
    this.controller = new XmlDataController();
    this.handler = new OptionHandler(this.controller);
  }

  /** Tests if the options are validated correctly. */
  @Test
  public void testValidateOptions() {
    // Tests if a valid sdt option is validated.
    List<String> parameters = new ArrayList<String>();
    parameters.add("DevEvent");
    Option sdtOption = SdtOption.createOption(controller, parameters);
    this.handler.addOption(sdtOption);
    Assert.assertTrue("The sdt option was not validated.", this.handler.isOptionsValid());

    // Tests if a duplicate option is validated.
    this.handler.addOption(sdtOption);
    Assert.assertFalse("A duplicate sdt option was validated.", this.handler.isOptionsValid());

    // Tests if an invalid file option is validated.
    parameters = new ArrayList<String>();
    parameters.add("FooPath");
    Option fileOption = FileOption.createOption(controller, parameters);
    this.handler.addOption(fileOption);
    Assert.assertFalse("The file option was validated.", this.handler.isOptionsValid());
  }

  /** Tests if all of the required options have been added. */
  @Test
  public void testHasRequiredOptions() {
    // First, test if no options fails.
    Assert.assertFalse("No options returned true.", this.handler.hasRequiredOptions());

    // Then, test if only the sdt options fails.
    Option sdtOption = SdtOption.createOption(controller, new ArrayList<String>());
    this.handler.addOption(sdtOption);
    Assert.assertFalse("Only an sdt option returned true.", this.handler.hasRequiredOptions());

    // Finally, test if all of the required attributes passes.
    Option fileOption = FileOption.createOption(controller, new ArrayList<String>());
    this.handler.addOption(fileOption);
    Assert.assertTrue("All the correct options returned false.", this.handler
        .hasRequiredOptions());
  }

  /**
   * Tests if the specified option strings return true if they are indeed an
   * option rather than a parameter.
   */
  @Test
  public void testIsOption() {
    Assert.assertTrue("-sdt failed the test to be an option.", this.handler.isOption("-sdt"));
    Assert.assertFalse("nohyphen passed the test to be an option.", this.handler
        .isOption("nohyphen"));
    Assert.assertFalse("An empty string passed the test to be an option", this.handler
        .isOption(""));
    Assert.assertFalse("Null passed the test to be an option", this.handler.isOption(null));
  }
}
