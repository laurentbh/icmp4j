package org.icmp4j.platform.unix.jni;

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
public class Icmp4jJNI {
  public String host;
  public int ttl;
  public int packetSize;
  public long timeOut;
  public int retCode;
  public int hasTimeout;
  public int bytes;
  public int returnTtl;
  public int rtt;
  public String address;
  public String errorMsg;
  public int errno;

  public native void icmp_start ();

  public native String icmp_test ();

  // Test Driver   
  public static void main (String args[]) {
    Icmp4jJNI test = new Icmp4jJNI ();
    test.host = "google.com";
    test.ttl = 50;
    test.packetSize = 64;
    test.timeOut = 9876;
    test.icmp_start ();
    System.out.println ("retCode: " + test.retCode);
    System.out.println ("errorMsg: " + test.errorMsg);
    System.out.println ("ttl: " + test.returnTtl);
    System.out.println ("rtt: " + test.rtt);
    System.out.println ("bytes: " + test.bytes);
    System.out.println ("address is: " + test.address);
  }
}
