package org.icmp4j.platform.unix.jni;

import org.icmp4j.IcmpPingRequest;
import org.icmp4j.IcmpPingResponse;
import org.icmp4j.platform.NativeBridge;
import org.icmp4j.util.JniUtil;



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
 * User: Laurent Buhler
 * Date: Jan 08, 2015
 * Time: 10:51:44 PM
 */
public class UnixJniNativeBridge extends NativeBridge {

  /**
   * The NativeBridge interface
   * Invoked to initialize this object
   */
  @Override
  public void initialize () {
    JniUtil.loadLibraryBestEffort ("icmp4jJNI");
    final Icmp4jJNI jniRequest = new Icmp4jJNI ();
    String version = jniRequest.icmp_test ();
    logger.info ("using icmp4jJNI v " + version );
  }


  @Override
  public IcmpPingResponse executePingRequest (IcmpPingRequest request) {
    final IcmpPingResponse response = new IcmpPingResponse ();
    final Icmp4jJNI jniRequest = new Icmp4jJNI ();
    jniRequest.host = request.getHost ();
    jniRequest.ttl = request.getTtl ();
    jniRequest.packetSize = request.getPacketSize ();
    jniRequest.timeOut = (int) request.getTimeout ();

    final long icmpSendEchoStartNanoTime = System.nanoTime ();

    jniRequest.icmp_start ();

    final long icmpSendEchoNanoDuration = System.nanoTime () - icmpSendEchoStartNanoTime;
    final long icmpSendEchoDuration = icmpSendEchoNanoDuration / 1000 / 1000;

    response.setDuration (icmpSendEchoDuration);
    response.setSuccessFlag (jniRequest.retCode == 1);
    response.setTimeoutFlag (jniRequest.hasTimeout == 1);
    response.setErrorMessage (jniRequest.errorMsg);
    response.setHost (jniRequest.address);
    response.setSize (jniRequest.bytes);
    response.setRtt (jniRequest.rtt);
    response.setTtl (jniRequest.ttl);
    return response;
  }

}
