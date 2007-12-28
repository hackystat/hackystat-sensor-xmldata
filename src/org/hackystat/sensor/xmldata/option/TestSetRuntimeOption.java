package org.hackystat.sensor.xmldata.option;

import java.util.ArrayList;
import java.util.List;

import org.hackystat.sensor.xmldata.XmlDataController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests if the SetRuntimeOption takes no parameters and is processed correctly.
 * @author aito
 * 
 */
public class TestSetRuntimeOption {
  /** The instances tested in this test class. */
  private Option runtimeOption = null;
  private XmlDataController controller = null;

  /** Sets each test case up. */
  @Before
  public void setup() {
    this.controller = new XmlDataController();
    this.runtimeOption = OptionFactory.getInstance(this.controller,
        SetRuntimeOption.OPTION_NAME, new ArrayList<String>());
  }

  /** Tests if the process method sets the correct option values. */
  @Test
  public void testProcess() {
    this.runtimeOption.process();
    Assert.assertEquals("The SetRuntimeOption object was not set to true.", Boolean.TRUE,
        this.controller.getOptionObject(Options.SET_RUNTIME));
  }

  /**
   * Tests if the isValid method always returns true when no arguments are
   * specified.
   */
  @Test
  public void testIsValid() {
    List<String> parameters = new ArrayList<String>();
    parameters.add("true");
    Option incorrectOption = new SetRuntimeOption(this.controller, parameters);
    Assert.assertFalse("The incorrect option is not valid.", incorrectOption.isValid());
    Assert.assertTrue("The correct option with no parameters returned false.",
        this.runtimeOption.isValid());
  }
}
