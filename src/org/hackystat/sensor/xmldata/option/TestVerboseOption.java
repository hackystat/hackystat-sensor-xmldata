package org.hackystat.sensor.xmldata.option;

import java.util.ArrayList;

import org.hackystat.sensor.xmldata.XmlDataController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests if the verbose option sets the correct option map parameters.
 * @author aito
 * 
 */
public class TestVerboseOption {
  /** The instances tested in this test class. */
  private Option verboseOption = null;
  private XmlDataController controller = null;

  /** Sets each test case up. */
  @Before
  public void setup() {
    this.controller = new XmlDataController();
    this.verboseOption = OptionFactory.getInstance(this.controller, VerboseOption.OPTION_NAME,
        new ArrayList<String>());
  }

  /** Tests if the process method sets the correct option values. */
  @Test
  public void testProcess() {
    Assert.assertEquals("The Verbose option object was pre-set.", null, this.controller
        .getOptionObject(Options.VERBOSE));
    this.verboseOption.process();
    Assert.assertEquals("The Verbose option object was not set to true.", Boolean.TRUE,
        this.controller.getOptionObject(Options.VERBOSE));
  }

  /**
   * Tests if the isValid method always returns true because this option does
   * not have any parameters.
   */
  @Test
  public void testIsValid() {
    Assert.assertTrue("This option always returns true.", this.verboseOption.isValid());
  }
}
