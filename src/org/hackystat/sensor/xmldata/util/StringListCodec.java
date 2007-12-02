package org.hackystat.sensor.xmldata.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides an encoder for transforming a List instance containing strings into a single string
 * (suitable for transmission via SOAP) and a decoder for reconstructing the List on the other side.
 * Individual strings must be less than MAX_STRING_LENGTH, and the number of strings in the list
 * must be less than MAX_NUM_STRINGS. Throws an exception if these maxima are exceeded, or if the
 * List to be decoded contains a non-String element, or if the String to be decoded is not correctly
 * formatted for decoding, or if the decoded string is not completely consumed by the decoding
 * process.
 * 
 * @author Philip M. Johnson
 * @version $Id: StringListCodec.java,v 1.1.1.1 2005/10/20 23:56:44 johnson Exp $
 */
public class StringListCodec {
  /** The maximum length of any individual string to be encoded. */
  public static final int MAX_STRING_LENGTH = 99999;
  /** The DecimalFormat pattern for this max size. */
  private static final String STRING_LENGTH_PATTERN = "00000";
  /** The number of characters used to represent length field. */
  private static final int STRING_LENGTH_FIELD_LENGTH = StringListCodec.STRING_LENGTH_PATTERN
      .length();

  /** The maximum number of strings that can be encoded. */
  public static final int MAX_NUM_STRINGS = 9999;
  /** The DecimalFormat pattern for this max strings. */
  private static final String NUM_STRINGS_PATTERN = "0000";
  /** The number of characters used to represent the total number of encoded strings. */
  private static final int NUM_STRINGS_FIELD_LENGTH = StringListCodec.NUM_STRINGS_PATTERN.length();

  /**
   * Provides a thread-local version of DecimalFormat to support multi-threading.
   * 
   * @author Philip Johnson
   */
  private static class ThreadLocalDecimalFormat extends ThreadLocal<Object> {
    /**
     * The initialization function.
     * 
     * @return The DecimalFormat instance for this thread.
     */
    @Override
    public Object initialValue() {
      return NumberFormat.getInstance();
    }
  }

  /** The thread-local instance wrapper for the decimalFormat object. */
  private static ThreadLocalDecimalFormat decimalFormat = new ThreadLocalDecimalFormat();

  /**
   * Gets the thread-local DecimalFormat instance.
   * 
   * @return The DecimalFormat instance for this thread.
   */
  private static DecimalFormat getDecimalFormat() {
    return (DecimalFormat) decimalFormat.get();
  }

  /**
   * Encodes the passed list of strings into a single string and returns it.
   * 
   * @param stringList a <code>List</code> value
   * @return a <code>String</code> value
   * @exception StringListCodecException If the list contains a non-String, or if the number of
   *            strings in the list exceeds MAX_NUM_STRINGS, or if the length of any individual
   *            string exceeds MAX_STRING_LENGTH.
   */
  public static String encode(List<String> stringList) throws StringListCodecException {
    // Make sure we don't have too many list elements.
    if (stringList.size() > MAX_NUM_STRINGS) {
      // Make darn sure someone hears about this even if the following exception is swallowed.
      System.out.println("ERROR: StringListCodec max num strings exceeded.");
      throw new StringListCodecException("String List exceeds " + MAX_NUM_STRINGS + " elements: "
          + stringList);
    }

    StringBuffer buff = new StringBuffer(computeBufferLength(stringList));

    // Encode the total number of list elements at the beginning of the string.
    getDecimalFormat().applyPattern(NUM_STRINGS_PATTERN);
    buff.append(((DecimalFormat) decimalFormat.get()).format(stringList.size()));

    // From now on, we encode using the following pattern.
    getDecimalFormat().applyPattern(STRING_LENGTH_PATTERN);

    // Loop through the elements and add them to the string buffer.
    for (String element : stringList) {
      // replace all occurences of "\r", "\r\n" with "\n"
      element = element.replaceAll("\r\n", "\n").replace('\r', '\n');

      // Second, make sure it's not too long.
      if (element.length() > MAX_STRING_LENGTH) {
        // Make darn sure someone hears about this even if the following exception is swallowed.
        System.out.println("ERROR: StringListCodec found a too long string.");
        throw new StringListCodecException("String list contains too long string: " + stringList);
      }

      // Now we add its size and the string itself to our buffer.
      buff.append(getDecimalFormat().format(element.length()));
      buff.append(element);
    }
    return buff.toString();
  }

  /**
   * Computes the exact length of the StringBuffer to allocate for this encoded string. This is
   * worth the expense since StringBuffers are 16 chars by default and double each time they're
   * exceeded, throwing away the old char array. A typical encoded string is gonna be 100 chars or
   * more, which means throwing away 4-5 char arrays each time if we don't figure out the size in
   * advance.
   * 
   * @param stringList a <code>List</code> value
   * @return an <code>int</code> value
   */
  private static int computeBufferLength(List<String> stringList) {
    int length = NUM_STRINGS_FIELD_LENGTH;
    for (String element : stringList) {
      length += element.length() + STRING_LENGTH_FIELD_LENGTH;
    }
    return length;
  }

  /**
   * Decodes the passed string, returning a List of strings.
   * 
   * @param encodedString The encoded list of strings.
   * @return A new list of strings.
   * @exception StringListCodecException If the passed encodedString is not encoded properly.
   */
  public static ArrayList<String> decode(String encodedString) throws StringListCodecException {
    // replace all occurences of "\r", "\r\n" with "\n"
    String newEncodedString = encodedString.replaceAll("\r\n", "\n").replace('\r', '\n');

    // Get the number of fields to be decoded.
    int numFields;
    try {
      numFields = Integer.parseInt(newEncodedString.substring(0, NUM_STRINGS_FIELD_LENGTH));
    }
    catch (Exception e) {
      throw new StringListCodecException("Error decoding numFields: " + newEncodedString, e);
    }
    // Make an array list to hold this number of elements.
    ArrayList<String> stringList = new ArrayList<String>(numFields);
    // Cursor always holds the index of next character to be processed in string.
    int cursor = NUM_STRINGS_FIELD_LENGTH;
    // Loop through the specified number of fields, extracting the field length and string,
    // and incrementing cursor.
    for (int i = 0; i < numFields; i++) {
      // First, get the field length.
      int fieldLength;
      String field;
      try {
        fieldLength = Integer.parseInt(newEncodedString.substring(cursor, cursor
            + STRING_LENGTH_FIELD_LENGTH));
      }
      catch (Exception e) {
        throw new StringListCodecException("Parse failed for field " + i + " and string "
            + newEncodedString, e);
      }

      // Second, extract that substring
      cursor += STRING_LENGTH_FIELD_LENGTH;
      try {
        field = newEncodedString.substring(cursor, cursor + fieldLength);
      }
      catch (Exception e) {
        throw new StringListCodecException("Could not extract field " + i + "from string "
            + newEncodedString, e);
      }

      // Third, add the field to the list, and increment the cursor.
      stringList.add(field);
      cursor += fieldLength;
    }

    // Make sure we've consumed the entire string.
    if (cursor != newEncodedString.length()) {
      throw new StringListCodecException("Encoded string too long: " + newEncodedString);
    }

    // We've extracted all of the fields, so now return the list.
    return stringList;
  }
}
