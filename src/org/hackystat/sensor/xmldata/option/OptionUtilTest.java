package org.hackystat.sensor.xmldata.option;

import java.util.ArrayList;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.sensor.xmldata.XmlDataController;
import org.hackystat.sensorshell.MultiSensorShell;
import org.hackystat.sensorshell.SensorShellProperties;
import org.hackystat.sensorshell.SensorShell;
import org.hackystat.sensorshell.Shell;
import org.hackystat.utilities.tstamp.Tstamp;
import org.hackystat.utilities.tstamp.TstampSet;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests if the option utility helper methods return the correct values.
 * @author aito
 * 
 */
public class OptionUtilTest {
  /**
   * Tests if the correct long value in milliseconds is returned based on the
   * timestamp.
   */
  @Test
  public void testGetTimestampInMillis() {
    // Test returning a timestamp long that is a string.
    try {
      long timestamp = new Date().getTime();
      Assert.assertTrue("The returned timestamp long is wrong.", timestamp == OptionUtil
          .getTimestampInMillis(String.valueOf(timestamp)));
    }
    catch (Exception e) {
      Assert.fail("Failed to get the timestamp in milliseconds.");
    }

    // Tests returning a timestamp long that is in the SimpleDateFormat:
    // MM/dd/yyyy-hh:mm:ss
    try {
      String timestamp = "07/07/1977-07:07:07";
      Assert.assertTrue("The returned timestamp long is wrong.", 237143227000L == OptionUtil
          .getTimestampInMillis(timestamp));
    }
    catch (Exception e) {
      Assert.fail("Failed to get the timestamp in milliseconds.");
    }

    // Tests sending an invalid SimpleDateFormat.
    try {
      String timestamp = "07/07/1977-07:07";
      OptionUtil.getTimestampInMillis(timestamp);
      Assert.fail("The simple date format is incorrect and should thrown an exception.");
    }
    catch (Exception e) {
      System.out.println("An invalid simple data format threw an exception.");
    }
  }

  /** Tests if the correct current timestamp is returned. */
  @Test
  public void testGetCurrentTimestamp() {
    // Next, let's test if unique current timestamps are returned.
    TstampSet tstampSet = new TstampSet();
    XMLGregorianCalendar timestamp1 = OptionUtil.getCurrentTimestamp(true, tstampSet);
    XMLGregorianCalendar timestamp2 = OptionUtil.getCurrentTimestamp(true, tstampSet);
    Assert.assertNotSame("The two timestamps are the same.", timestamp1, timestamp2);
  }

  /** Tests if the specified timestamp is massaged to valid values correctly. */
  @Test
  public void testMassageTimestamp() {
    // Tests if the correct, non-unique, XmlGregorianCalendar is returned.
    long timestamp = new Date().getTime();
    Assert.assertEquals("The returned calendar instance is incorrect.", Tstamp
        .makeTimestamp(timestamp), OptionUtil.massageTimestamp(false, new TstampSet(),
        timestamp));

    // Tests if unique XmlGregorianCalendars are returned.
    TstampSet tstampSet = new TstampSet();
    Assert.assertNotSame("The returned calendar instance is incorrect.", tstampSet
        .getUniqueTstamp(timestamp), OptionUtil.massageTimestamp(true, tstampSet, timestamp));
  }

  /** Tests if the correct sensorshell instance is returned. */
  @Test
  public void testCreateShell() {
    try {
      // First, create the controller used to determine which shell to use.
      XmlDataController controller = new XmlDataController();
      Option option = OptionFactory.getInstance(controller, MultiShellOption.OPTION_NAME,
          new ArrayList<String>());

      // Tests if a normal SensorShell is used.
      Shell shell = OptionUtil.createShell(new SensorShellProperties(), controller);
      Assert.assertTrue("The returned shell is not a SensorShell instance.",
          shell instanceof SensorShell);

      // Tests if a MultiSensorShell is used when the option is set.
      option.process();
      shell = OptionUtil.createShell(new SensorShellProperties(), controller);
      Assert.assertTrue("The returned shell is not a MultiSensorShell instance.",
          shell instanceof MultiSensorShell);
    }
    catch (Exception e) {
      e.printStackTrace();
      Assert.fail("Failed to create a Shell instance.");
    }
  }
}
