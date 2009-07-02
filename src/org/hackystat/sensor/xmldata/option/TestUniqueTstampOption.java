package org.hackystat.sensor.xmldata.option;

import java.util.ArrayList;
import java.util.List;

import org.hackystat.sensor.xmldata.XmlDataController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests if the UniqueTstampOption sets the correct option map values.
 * @author aito
 * 
 */
public class TestUniqueTstampOption {
  /** The instances tested in this test class. */
  private Option uniqueOption = null;
  private XmlDataController controller = null;

  /** Sets each test case up. */
  @Before
  public void setUp() {
    this.controller = new XmlDataController();
    this.uniqueOption = OptionFactory.getInstance(this.controller,
        UniqueTstampOption.OPTION_NAME, new ArrayList<String>());
  }

  /** Tests if the process method sets the correct option values. */
  @Test
  public void testProcess() {
    Assert.assertEquals("The UniqueTimestamp option object was not false.", Boolean.FALSE,
        this.controller.getOptionObject(Options.UNIQUE_TSTAMP));
    this.uniqueOption.process();
    Assert.assertEquals("The UniqueTimestamp option object was not set to true.",
        Boolean.TRUE, this.controller.getOptionObject(Options.UNIQUE_TSTAMP));
  }

  /**
   * Tests if the isValid method always returns true when no arguments are
   * specified.
   */
  @Test
  public void testIsValid() {
    List<String> parameters = new ArrayList<String>();
    parameters.add("true");
    Option incorrectOption = new UniqueTstampOption(this.controller, parameters);
    Assert.assertFalse("The incorrect option is not valid.", incorrectOption.isValid());
    Assert.assertTrue("The correct option with no parameters returned false.",
        this.uniqueOption.isValid());
  }
}
