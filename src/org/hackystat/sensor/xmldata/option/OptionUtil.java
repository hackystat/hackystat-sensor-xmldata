package org.hackystat.sensor.xmldata.option;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.utilities.tstamp.Tstamp;
import org.hackystat.utilities.tstamp.TstampSet;

/**
 * The option utility class that contains methods used by multiple options.
 * @author aito
 * 
 */
public class OptionUtil {
  /** Private constructor that prevents instantiation. */
  private OptionUtil() {
  }

  /**
   * Returns the string containing the information stored in the key-value
   * mapping of sensor data. This string is helpful when running this option in
   * verbose mode.
   * @param keyValMap the map used to generate the returned string.
   * @return the informative string.
   */
  public static String getMapVerboseString(Map<String, String> keyValMap) {
    if (!keyValMap.isEmpty()) {
      String verboseString = "[";
      for (Map.Entry<String, String> entry : keyValMap.entrySet()) {
        verboseString = verboseString.concat(entry.getKey() + "=" + entry.getValue()) + ", ";
      }

      // Remove the last ', ' from the string.
      verboseString = verboseString.substring(0, verboseString.length() - 2);
      return verboseString.concat("]");
    }
    return "";
  }

  /**
   * Returns the long value of the specified timestamp string representation.
   * This method expects the timestamp string to be a long or in the
   * SimpleDateFormat: MM/dd/yyyy-hh:mm:ss. If the timestamp does not fit either
   * specification, a runtime exception is thrown.
   * @param timestamp the specified string representation of a timestamp.
   * @return the long value of the specified string timestamp.
   * @throws Exception thrown if the specified timestamp string is not in a
   * valid SimpleDateFormat.
   */
  public static long getTimestampInMillis(String timestamp) throws Exception {
    if (OptionUtil.isTimestampLong(timestamp)) {
      return Long.valueOf(timestamp);
    }
    else if (OptionUtil.isTimestampSimpleDate(timestamp)) {
      SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy-hh:mm:ss", Locale.US);
      return format.parse(timestamp, new ParsePosition(0)).getTime();
    }
    String msg = "The timestamp must either be specified as a "
        + "long or in the format: MM/dd/yyyy-hh:mm:ss";
    throw new Exception(msg);
  }

  /**
   * Returns true if the specified timestamp string representation is in long
   * format.
   * @param timestamp the string to test.
   * @return true if the timestamp is a long, false if not.
   */
  private static boolean isTimestampLong(String timestamp) {
    try {
      Long.valueOf(timestamp);
      return true;
    }
    catch (NumberFormatException nfe) {
      return false;
    }
  }

  /**
   * Returns true if the specified timestamp is in the SimpleDateFormat:
   * MM/dd/yyyy-hh:mm:ss
   * @param timestamp the timestamp to test.
   * @return true if the timestamp is in the specified SimpleDateFormat, false
   * if not.
   */
  private static boolean isTimestampSimpleDate(String timestamp) {
    try {
      SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy-hh:mm:ss", Locale.US);
      format.parse(timestamp, new ParsePosition(0)).getTime();
      return true;
    }
    catch (NullPointerException npe) {
      return false;
    }
  }

  /**
   * Returns the current timestamp based on the specified parameters.
   * @param isUnique if this is true, a unique timestamp, based on the specified
   * tstampSet, is returned.
   * @param tstampSet the set of timestamps that is managed to ensure that a
   * unique timestamp is generated.
   * @return the XmlGregorianCalendar instance representing the current
   * timestamp.
   */
  public static XMLGregorianCalendar getCurrentTimestamp(boolean isUnique, TstampSet tstampSet) {
    if (isUnique) {
      return Tstamp.makeTimestamp(tstampSet.getUniqueTstamp(new Date().getTime()));
    }
    return Tstamp.makeTimestamp();
  }

  /**
   * "Massages" the specified timestamp by using the specified parameters.
   * @param isUnique if this is true, the specified timestamp is changed to be
   * unique based on the specified tstampSet.
   * @param tstampSet the set of timestamps that is managed to ensure that a
   * unique timestamp is generated.
   * @param timestamp the timestamp to massage.
   * @return the XmlGregorianCalendar instance representing the current
   * timestamp.
   */
  public static XMLGregorianCalendar massageTimestamp(Boolean isUnique, TstampSet tstampSet,
      long timestamp) {
    if (isUnique) {
      return Tstamp.makeTimestamp(tstampSet.getUniqueTstamp(timestamp));
    }
    return Tstamp.makeTimestamp(timestamp);
  }
}
