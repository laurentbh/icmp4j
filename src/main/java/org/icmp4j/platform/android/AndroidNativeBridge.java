package org.icmp4j.platform.android;

import java.util.List;

import org.icmp4j.platform.NativeBridge;
import org.icmp4j.IcmpPingResponse;
import org.icmp4j.IcmpPingRequest;
import org.icmp4j.IcmpPingUtil;
import org.icmp4j.util.ProcessUtil;
import org.icmp4j.util.StringUtil;

/**
 * icmp4j
 * http://www.icmp4j.org
 * Copyright 2009 and beyond, Sal Ingrilli at the icmp4j
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
 * Date: Dec 23, 2014
 * Time: 10:57:59 PM
 */
public class AndroidNativeBridge extends NativeBridge {

  /**
   * The NativeBridge interface
   * 
   * Executes the given icmp ECHO request
   * This call blocks until a response is received or a timeout is reached
   * 
   * @param request
   * @return IcmpEchoResponse
   */
  @Override
  public IcmpPingResponse executePingRequest (final IcmpPingRequest request) {

    // handle exceptions
    try {

      // request
      final String host = request.getHost ();
      final int timeout = new Long (request.getTimeout ()).intValue ();
      final int timeoutAsSeconds = timeout / 1000;
      final int timeoutAsSeconds2 = timeoutAsSeconds > 0 ?
        timeoutAsSeconds :
        1;
      final int packetSize = request.getPacketSize ();

      // execute the ping command
      final String command = "ping -c 1 -s " + packetSize + " -w " + timeoutAsSeconds2 + " " + host;
      final long icmpSendEchoStartTime = System.currentTimeMillis ();
      final List<String> stringList = ProcessUtil.executeProcessAndGetOutputAsStringList (command);
      final long icmpSendEchoDuration = System.currentTimeMillis () - icmpSendEchoStartTime;

      // check for timeout
      final boolean timeoutFlag = icmpSendEchoDuration >= timeout;
      if (timeoutFlag) {
        return IcmpPingUtil.createTimeoutIcmpPingResponse (icmpSendEchoDuration);
      }

      // delegate to a method that can be unit tested
      return executePingRequest (stringList);
    }
    catch (final Exception e) {

      // propagate
      throw new RuntimeException (e);
    }
  }

  /**
   * Executes the given request
   * @param stringList
   * @return IcmpEchoResponse
   */
  IcmpPingResponse executePingRequest (final List<String> stringList) {

    // look for the first line with some output
    // sample output from DEBIAN 6
    //   ping existing host
    //   root@database:~# ping -c 1 -s 32 -w 5 www.google.com
    //   PING www.google.com (74.125.224.211) 32(60) bytes of data.
    //   40 bytes from lax02s02-in-f19.1e100.net (74.125.224.211): icmp_req=1 ttl=56 time=47.2 ms
    //
    //   ping existing host by dekker on sf.net:
    //   ping google.com
    //   64 bytes from 173.194.113.207: icmp_seq=1 ttl=59 time=237
    //
    //   ping existing host by sal on android:
    //   u0_a188@afyonltetmo:/ $ping -c 1 -s 32 -w 5 www.google.com
    //   40 bytes from lax17s04-in-f4.1e100.net (216.58.219.36): icmp_seq=1 ttl=55 time=40.7 ms
    // 
    //   ping non-existing host
    //   ping -c 1 -s 32 -w 5 www.googgle.com
    //   ping: unknown host www.googgle.com
    for (final String string : stringList) {

      // discriminate against non-ping lines
      final int icmpReqIndex = string.indexOf ("icmp_req=");
      final int icmpSeqIndex = string.indexOf ("icmp_seq=");
      if (icmpReqIndex < 0 && icmpSeqIndex < 0) {
        continue;
      }

      // parse response
      int size = 0;
      {
        final int bytesIndex = string.indexOf (" bytes");
        if (bytesIndex > 0) {
          final String sizeAsString = string.substring (0, bytesIndex);
          size = Integer.parseInt (sizeAsString);
        }
      }
      final String responseAddress = StringUtil.parseString (
        string,
        "from ",
        " ");
      final String ttlAsString = StringUtil.parseString (
        string,
        "ttl=",
        " ");
      final int ttl = Integer.parseInt (ttlAsString);
      final String rttAsString = StringUtil.parseString (
        string,
        "time=",
        "ms");
      final String rttAsString2 = rttAsString.trim ();
      final Float rttAsFloat = Float.parseFloat (rttAsString2);
      final int rtt = rttAsFloat.intValue ();

      // objectify
      final IcmpPingResponse response = new IcmpPingResponse ();
      response.setHost (responseAddress);
      response.setErrorMessage (null);
      response.setRtt (rtt);
      response.setSize (size);
      response.setSuccessFlag (true);
      response.setTtl (ttl);

      // done
      return response;
    }

    // not found - if there is at least one line, use that as the error message
    // noinspection LoopStatementThatDoesntLoop
    for (final String string : stringList) {

      // objectify
      final IcmpPingResponse response = new IcmpPingResponse ();
      response.setErrorMessage (string);
      response.setSuccessFlag (false);

      // done
      return response;
    }

    // no results found
    {
      // objectify
      final IcmpPingResponse response = new IcmpPingResponse ();
      response.setErrorMessage ("No results could be parsed");
      response.setSuccessFlag (false);

      // done
      return response;
    }
  }
}