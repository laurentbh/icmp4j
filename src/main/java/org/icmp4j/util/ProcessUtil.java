package org.icmp4j.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.List;
import java.util.LinkedList;

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
 * Time: 9:41:55 PM
 */
public class ProcessUtil {

  /**
   * Executes the given command
   * Returns the output of the process as a List of strings
   * @param command
   * @return List<String>
   */
  public static List<String> executeProcessAndGetOutputAsStringList (final String command) {

    // handle exceptions
    try {

      // delegate
      final Runtime runtime = Runtime.getRuntime();
      final Process process = runtime.exec (command);

      // extract output
      final InputStream inputStream = process.getInputStream ();
      final InputStreamReader inputStreamReader = new InputStreamReader (inputStream);
      final BufferedReader bufferedReader = new BufferedReader (inputStreamReader);
      final List<String> stringList = new LinkedList<String> ();
      while (true) {

        // next line
        final String string = bufferedReader.readLine ();
        if (string == null) {
          break;
        }
        
        // track
        stringList.add (string);
      }

      // done
      return stringList;
    }
    catch (final Exception e) {

      // propagate
      throw new RuntimeException (e);
    }
  }
}