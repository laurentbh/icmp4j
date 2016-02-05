package org.icmp4j.platform.java;

import java.net.InetAddress;

import org.icmp4j.IcmpPingResponse;
import org.icmp4j.IcmpPingRequest;
import org.icmp4j.IcmpPingUtil;
import org.icmp4j.platform.NativeBridge;

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
 * Time: 9:03:37 PM
 */
public class JavaNativeBridge extends NativeBridge {

  /**
   * The NativeBridge interface
   * 
   * Executes the given icmp ECHO request
   * This call blocks until a response is received or a timeout is reached
   * 
   * The jna implementation adapted from:
   *   http://hp.vector.co.jp/authors/VA033015/jnasamples.html
   * 
   * @param request
   * @return IcmpEchoResponse
   */
  @Override
  public IcmpPingResponse executePingRequest (final IcmpPingRequest request) {

    // handle exceptions
    try {

      // delegate
      final String host = request.getHost ();
      final InetAddress address = InetAddress.getByName (host);
      final int timeout = new Long (request.getTimeout ()).intValue ();
      final long pingStartNanoTime = System.nanoTime ();
      final long icmpSendEchoStartTime = System.currentTimeMillis ();
      final boolean successFlag = address.isReachable (timeout);
      final long icmpSendEchoDuration = System.currentTimeMillis () - icmpSendEchoStartTime;
      final long rttNanos = System.nanoTime () - pingStartNanoTime;
      final int rtt = new Long (rttNanos / (1000 * 1000)).intValue ();
      
      // check for timeout
      final boolean timeoutFlag = icmpSendEchoDuration >= timeout;
      if (timeoutFlag) {
        return IcmpPingUtil.createTimeoutIcmpPingResponse (icmpSendEchoDuration);
      }

      // objectify
      final IcmpPingResponse response = new IcmpPingResponse ();
      response.setHost (null);
      response.setErrorMessage (null);
      response.setRtt (rtt);
      response.setSize (0);
      response.setSuccessFlag (successFlag);
      response.setTtl (0);

      // done
      return response;
    }
    catch (final Exception e) {

      // propagate
      throw new RuntimeException (e);
    }
  }
}