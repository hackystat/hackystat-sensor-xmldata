package org.hackystat.sensor.xmldata.option;

import java.util.ArrayList;
import java.util.List;

import org.hackystat.sensor.xmldata.XmlDataController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests if the MultiShellOption receives the correct arguments and is processed
 * as intended.
 * @author aito
 * 
 */
public class TestMultiShellOption {
  /** The instances tested in this test class. */
  private Option option = null;
  private XmlDataController controller = null;

  /** Sets each test case up. */
  @Before
  public void setUp() {
    this.controller = new XmlDataController();
    this.option = OptionFactory.getInstance(this.controller, MultiShellOption.OPTION_NAME,
        new ArrayList<String>());
  }

  /** Tests if the process method sets the correct option values. */
  @Test
  public void testProcess() {
    Assert.assertEquals("The MultiShell option object was pre-set.", null, this.controller
        .getOptionObject(Options.MULTI_SHELL));
    this.option.process();
    Assert.assertEquals("The MultiShell option object was not set to true.", Boolean.TRUE,
        this.controller.getOptionObject(Options.MULTI_SHELL));
  }

  /**
   * Tests if the isValid method always returns true when no arguments are
   * specified or false if any arguments are specified.
   */
  @Test
  public void testIsValid() {
    List<String> parameters = new ArrayList<String>();
    parameters.add("true");
    Option incorrectOption = new MultiShellOption(this.controller, parameters);
    Assert.assertFalse("The incorrect option is not valid.", incorrectOption.isValid());
    Assert.assertTrue("The correct option with no arguments returned false.", this.option
        .isValid());
  }
}
