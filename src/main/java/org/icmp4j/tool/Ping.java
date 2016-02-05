package org.icmp4j.tool;

import org.icmp4j.IcmpPingUtil;
import org.icmp4j.IcmpPingRequest;
import org.icmp4j.IcmpPingResponse;
import org.icmp4j.util.ArgUtil;

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
 * Time: 6:43:43 PM
 */
public class Ping {

  /**
   * The Java interface
   * 
   * To test:
   * cd c:\dev\icmp4j\trunk\icmp4j\output\tool
   * java -cp * -Djava.library.path=. org.icmp4j.tool.Ping www.google.com
   * 
   * @param args
   */
  public static void main (final String[] args){

    // handle exceptions
    try {

      // extract request parameters
      // -t: windows ping.exe repeats until stopped, otherwise default to 4
      final String host = args.length > 0 ?
        args [args.length - 1] :
        "google.com";
      final int maxCount = ArgUtil.findArgument (args, "-t") ?
        Integer.MAX_VALUE :
        4;

      // request
      final IcmpPingRequest request = IcmpPingUtil.createIcmpPingRequest ();
      request.setHost (host);

      // repeat 4 times by default
      for (int count = 1; count <= maxCount; count ++) {

        // delegate
        final IcmpPingResponse response = IcmpPingUtil.executePingRequest (request);

        // log
        final String formattedResponse = IcmpPingUtil.formatResponse (response);
        System.out.println (formattedResponse);

        // rest
        Thread.sleep (1000);
      }
    }
    catch (final Throwable t){

      // log
      t.printStackTrace ();
    }
  }
}