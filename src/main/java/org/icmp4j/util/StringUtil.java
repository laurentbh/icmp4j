package org.icmp4j.util;

/**
 * Internet Control Message Protocol for Java (ICMP4J)
 * http://www.icmp4j.org
 * Copyright 2009 and beyond, icmp4j
 * <p/>
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation as long as:
 * 1. You credit the original author somewhere within your product or website
 * 2. The credit is easily reachable and not burried deep
 * 3. Your end-user can easily see it
 * 4. You register your name (optional) and company/group/org name (required)
 * at http://www.icmp4j.org
 * 5. You do all of the above within 4 weeks of integrating this software
 * 6. You contribute feedback, fixes, and requests for features
 * <p/>
 * If/when you derive a commercial gain from using this software
 * please donate at http://www.icmp4j.org
 * <p/>
 * If prefer or require, contact the author specified above to:
 * 1. Release you from the above requirements
 * 2. Acquire a commercial license
 * 3. Purchase a support contract
 * 4. Request a different license
 * 5. Anything else
 * <p/>
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, similarly
 * to how this is described in the GNU Lesser General Public License.
 * <p/>
 * User: Sal Ingrilli
 * Date: May 23, 2014
 * Time: 10:24:30 PM
 */
public class StringUtil {
  
  // my attributes
  private static String newLine;
  
  // my attributes
  public static void setNewLine (final String newLine) { StringUtil.newLine = newLine; }
  public static String getNewLine () { return newLine; }
  
  // static initializer
  static
  {
    newLine = System.getProperty ("line.separator");
  }
  
  /**
   * Returns true if the given string is null or if size == 0 when trimmed
   *
   * @param value
   * @return boolean
   */
  public static boolean isSameAsEmpty (final String value) {

    return
      value == null ||
      value.trim ().length () == 0;
  }
  
  /**
   * Extracts and returns the string between beginDelimiter and endDelimiter. For example:
   *   string        : "64 bytes from 66.102.7.99: icmp_seq=0 ttl=56 time=27.252 ms"
   *   beginDelimiter:          "from "
   *     endDelimiter:                          ":"
   *           return:               "66.102.7.99"
   * @param string
   * @param beginDelimiter
   * @param endDelimiter
   * @return String
   */
  public static String parseString (
    final String string,
    final String beginDelimiter,
    final String endDelimiter) {

    // look for the beginning of the string to extract
    final int beginDelimiterIndex = string.indexOf (beginDelimiter);
    if (beginDelimiterIndex < 0) {
      return null;
    }

    // look for the end of the string to extract
    final int fromIndex = beginDelimiterIndex + beginDelimiter.length ();
    final int endDelimiterIndex = string.indexOf (
      endDelimiter,
      fromIndex);
    if (endDelimiterIndex < 0) {
      return null;
    }

    // extract
    return string.substring (fromIndex, endDelimiterIndex);
  }
}