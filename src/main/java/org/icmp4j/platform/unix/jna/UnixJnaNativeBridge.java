package org.icmp4j.platform.unix.jna;

import org.icmp4j.Icmp4jUtil;
import org.icmp4j.IcmpPingRequest;
import org.icmp4j.IcmpPingResponse;
import org.icmp4j.platform.NativeBridge;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

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
 * User: Laurent Buhler
 * Date: Jan 08, 2015
 * Time: 8:59:11 PM
 */
public class UnixJnaNativeBridge extends NativeBridge {
  
  /**
	   * The NativeBridge interface
	   * Invoked to initialize this object
	   */
	  @Override
	  public void initialize () {

	   // delegate
		 LibraryUtil.initialize ();
		 final IcmpLibrary icmpLibrary = LibraryUtil.getIcmpLibrary ();
		    
		 PointerByReference ptrRef = new PointerByReference ();
		 icmpLibrary.icmp4j_exist (ptrRef);
		 Pointer p = ptrRef.getValue ();
		 String version = p.getString (0);
     logger.info ("using icmp4jJNA v " + version);
     icmpLibrary.icmp4j_exist_free (p);
	 }
	  
	
	@Override
	public IcmpPingResponse executePingRequest(IcmpPingRequest request) {
		
    final IcmpLibrary icmpLibrary = LibraryUtil.getIcmpLibrary ();
		
		IcmpLibrary.Icmp4jStruct.ByReference ref = new IcmpLibrary.Icmp4jStruct.ByReference ();
		ref.host = request.getHost ();
		ref.ttl = request.getTtl ();
		ref.packetSize = request.getPacketSize ();
		ref.timeOut = new NativeLong (request.getTimeout ());
		
		final long icmpSendEchoStartNanoTime = System.nanoTime ();
		
		final IcmpPingResponse response = new IcmpPingResponse ();
		
		icmpLibrary.icmp4j_start (ref);
		
		final long icmpSendEchoNanoDuration = System.nanoTime () - icmpSendEchoStartNanoTime;
	  final long icmpSendEchoDuration = icmpSendEchoNanoDuration / 1000 / 1000;
		  
		response.setSuccessFlag ( ref.retCode == 1 );
		response.setTimeoutFlag ( ref.hasTimeout == 1 );
		response.setErrorMessage ( ref.errorMsg );
		response.setHost ( ref.address );
		response.setSize ( ref.bytes );
		response.setRtt ( ref.rtt );
		response.setTtl ( ref.ttl );
		icmpLibrary.icmp4j_free ( ref );
		
	  response.setDuration ( icmpSendEchoDuration );
		return response;
	}

}
