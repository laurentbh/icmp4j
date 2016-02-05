package org.icmp4j.platform.linux;

import java.util.List;
import java.util.LinkedList;

import org.icmp4j.IcmpPingResponse;
import org.icmp4j.platform.unix.LinuxProcessNativeBridge;

import junit.framework.TestCase;

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
 * Time: 10:46:34 PM
 */
public class LinuxNativeBridgeTest extends TestCase {

  /**
   * Tests executePingRequest ()
   */
  public void test_executePingRequest_normal () {

    final List<String> stringList = new LinkedList<String> ();
    stringList.add ("PING www.google.com (74.125.224.211) 32(60) bytes of data.");
    stringList.add ("40 bytes from lax02s02-in-f19.1e100.net (74.125.224.211): icmp_req=1 ttl=56 time=47.2 ms");
    debug ("output:");
    for (final String string : stringList) {
      debug ("string: " + string);
    }

    final LinuxProcessNativeBridge linuxProcessNativeBridge = new LinuxProcessNativeBridge ();
    final IcmpPingResponse response = linuxProcessNativeBridge.executePingRequest (stringList);

    {
      final String errorMessage = response.getErrorMessage ();
      debug ("errorMessage: " + errorMessage);
      assertEquals (
        null + " == " + errorMessage,
        null,
        errorMessage);
    }

    {
      final String host = response.getHost ();
      debug ("host: " + host);
      assertEquals (
        "lax02s02-in-f19.1e100.net == " + host,
        "lax02s02-in-f19.1e100.net",
        host);
    }
    
    {
      final int rtt = response.getRtt ();
      debug ("rtt: " + rtt);
      assertEquals (
        47 + " == " + rtt,
        47,
        rtt);
    }
    
    {
      final int size = response.getSize ();
      debug ("size: " + size);
      assertEquals (
        40 + " == " + size,
        40,
        size);
    }
    
    {
      final boolean successFlag = response.getSuccessFlag ();
      debug ("successFlag: " + successFlag);
      assertEquals (
        true + " == " + successFlag,
        true,
        successFlag);
    }
    
    {
      final int ttl = response.getTtl ();
      debug ("ttl: " + ttl);
      assertEquals (
        56 + " == " + ttl,
        56,
        ttl);
    }
  }
  
  /**
   * Tests executePingRequest ()
   * WARNING: the difference with these Linux distributions is that for the sequence number
   * they use "icmp_seq" and NOT "icmp_req"
   */
  public void test_executePingRequest_Mint17_and_ArchLinux () {

    final List<String> stringList = new LinkedList<String> ();
    stringList.add ("PING www.google.com (74.125.224.211) 32(60) bytes of data.");
    stringList.add ("40 bytes from lax02s02-in-f19.1e100.net (74.125.224.211): icmp_seq=1 ttl=56 time=47.2 ms");
    debug ("output:");
    for (final String string : stringList) {
      debug ("string: " + string);
    }

    final LinuxProcessNativeBridge linuxProcessNativeBridge = new LinuxProcessNativeBridge ();
    final IcmpPingResponse response = linuxProcessNativeBridge.executePingRequest (stringList);

    {
      final String errorMessage = response.getErrorMessage ();
      debug ("errorMessage: " + errorMessage);
      assertEquals (
        null + " == " + errorMessage,
        null,
        errorMessage);
    }

    {
      final String host = response.getHost ();
      debug ("host: " + host);
      assertEquals (
        "lax02s02-in-f19.1e100.net == " + host,
        "lax02s02-in-f19.1e100.net",
        host);
    }
    
    {
      final int rtt = response.getRtt ();
      debug ("rtt: " + rtt);
      assertEquals (
        47 + " == " + rtt,
        47,
        rtt);
    }
    
    {
      final int size = response.getSize ();
      debug ("size: " + size);
      assertEquals (
        40 + " == " + size,
        40,
        size);
    }
    
    {
      final boolean successFlag = response.getSuccessFlag ();
      debug ("successFlag: " + successFlag);
      assertEquals (
        true + " == " + successFlag,
        true,
        successFlag);
    }
    
    {
      final int ttl = response.getTtl ();
      debug ("ttl: " + ttl);
      assertEquals (
        56 + " == " + ttl,
        56,
        ttl);
    }
  }
  
  /**
   * Tests executePingRequest ()
   */
  public void test_executePingRequest_unknownHost () {

    final List<String> stringList = new LinkedList<String> ();
    stringList.add ("ping: unknown host www.googgle.com");
    debug ("output:");
    for (final String string : stringList) {
      debug ("string: " + string);
    }

    final LinuxProcessNativeBridge linuxProcessNativeBridge = new LinuxProcessNativeBridge ();
    final IcmpPingResponse response = linuxProcessNativeBridge.executePingRequest (stringList);

    {
      final String errorMessage = response.getErrorMessage ();
      debug ("errorMessage: " + errorMessage);
      assertEquals (
        "ping: unknown host www.googgle.com" + " == " + errorMessage,
        "ping: unknown host www.googgle.com",
        errorMessage);
    }
    
    {
      final boolean successFlag = response.getSuccessFlag ();
      debug ("successFlag: " + successFlag);
      assertEquals (
        false + " == " + successFlag,
        false,
        successFlag);
    }
  }

  /**
   * Uniformly logs the given debug string
   * @param string
   */
  private void debug (final String string) {
    
    System.out.println ("<DEBUG> <" + string + ">");
  }
}