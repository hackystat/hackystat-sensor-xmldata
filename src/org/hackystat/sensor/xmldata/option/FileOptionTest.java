package org.hackystat.sensor.xmldata.option;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.hackystat.sensor.xmldata.XmlDataController;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests if the FileOption operates as intended.
 * @author aito
 * 
 */
public class FileOptionTest {
  /** Tests if isValid returns false when no arguments are specified. */
  @Test
  public void testNoFileArguments() {
    XmlDataController controller = new XmlDataController();
    Option fileOption = FileOption.createOption(controller, new ArrayList<String>());
    Assert.assertFalse("A file option should have at least 1 file argument.", fileOption
        .isValid());
  }

  /** Tests if isValid returns true if one file is specified. */
  @Test
  public void testOneFileArgument() {
    XmlDataController controller = new XmlDataController();
    URL testUrl = XmlDataController.class.getResource("testdataset/testdata.xml");
    List<String> parameters = new ArrayList<String>();
    parameters.add(testUrl.getPath());
    Option fileOption = FileOption.createOption(controller, parameters);
    Assert.assertTrue("A file option with one file should be valid.", fileOption.isValid());
  }

  /** Tests if isValid returns true if more than one file is specified. */
  @Test
  public void testMultipleFileArguments() {
    XmlDataController controller = new XmlDataController();
    URL testUrl = XmlDataController.class.getResource("testdataset/testdata.xml");
    URL testUrl2 = XmlDataController.class.getResource("testdataset/testdata2.xml");
    List<String> parameters = new ArrayList<String>();
    parameters.add(testUrl.getPath());
    parameters.add(testUrl2.getPath());
    Option fileOption = FileOption.createOption(controller, parameters);
    Assert.assertTrue("A file option with more than one file should be valid.", fileOption
        .isValid());
  }

  /** Tests if isValid returns false if a non-existant file is specified. */
  @Test
  public void testNonExistantFile() {
    XmlDataController controller = new XmlDataController();
    List<String> parameters = new ArrayList<String>();
    parameters.add("Test.xml");
    Option fileOption = FileOption.createOption(controller, parameters);
    Assert.assertFalse("A non-existant file should invalidate this option.", fileOption
        .isValid());
  }
}
