package org.hackystat.sensor.xmldata.option;

import java.util.ArrayList;
import java.util.List;

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
  public void setUp() {
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
   * Tests if the isValid method always returns true when no arguments are
   * specified.
   */
  @Test
  public void testIsValid() {
    List<String> parameters = new ArrayList<String>();
    parameters.add("true");
    Option incorrectOption = new VerboseOption(this.controller, parameters);
    Assert.assertFalse("The incorrect option is not valid.", incorrectOption.isValid());
    Assert.assertTrue("The correct option with no parameters returned false.",
        this.verboseOption.isValid());
  }
}
