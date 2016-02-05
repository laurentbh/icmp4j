package org.icmp4j.platform.unix;

import org.icmp4j.IcmpPingRequest;
import org.icmp4j.IcmpPingResponse;
import org.icmp4j.exception.AssertRuntimeException;
import org.icmp4j.platform.NativeBridge;
import org.icmp4j.platform.unix.jna.UnixJnaNativeBridge;
import org.icmp4j.platform.unix.jni.UnixJniNativeBridge;
import org.icmp4j.util.ExceptionUtil;

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
 * Date: Jan 30, 2016
 * Time: 10:51:44 PM
 */

public class UnixNativeBridge extends NativeBridge {

  // my attributes
  private NativeBridge nativeBridge;

  /**
   * The NativeBridge interface
   * Invoked to initialize this object
   */
  @Override
  public void initialize () {

    // helpers
    logger.debug ("initialize ()");

    // try JNI
    try {

      logger.debug ("trying delegate UnixJniNativeBridge");
      final NativeBridge nativeBridge = new UnixJniNativeBridge ();
      nativeBridge.initialize ();
      this.nativeBridge = nativeBridge;
      return;
    }
    catch (final Throwable t) {

      // log
      logger.warn ("delegate UnixJniNativeBridge not avilable: " + ExceptionUtil.getMessage (t));
    }
    
    // try JNA
    try {

      logger.debug ("trying delegate UnixJnaNativeBridge");
      final NativeBridge nativeBridge = new UnixJnaNativeBridge ();
      nativeBridge.initialize ();
      this.nativeBridge = nativeBridge;
      return;
    }
    catch (final Throwable t) {

      // log
      logger.warn ("delegate UnixJnaNativeBridge not avilable: " + ExceptionUtil.getMessage (t));
    }
    
    // fallback to the process execution
    // as of build 1019, this initialization should ALWAYS work because nothing happens in their initialize ()
    try {

      logger.debug ("trying delegate *ProcessNativeBridge");
      final NativeBridge nativeBridge = ProcessNativeBridgeFactory.createNativeBridge ();
      nativeBridge.initialize ();
      this.nativeBridge = nativeBridge;
      return;
    }
    catch (final Throwable t) {

      // log
      logger.warn ("delegate *ProcessNativeBridge not avilable: " + ExceptionUtil.getMessage (t));
    }

    // all failed - this should not happen because the *ProcessNativeBridge should always work
    throw new AssertRuntimeException ("all delegates failed");
  }

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
  public IcmpPingResponse executePingRequest(IcmpPingRequest request) {

    // delegate
    return nativeBridge.executePingRequest(request);
  }
}