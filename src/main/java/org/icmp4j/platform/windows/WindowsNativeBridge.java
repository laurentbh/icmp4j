package org.icmp4j.platform.windows;

import java.net.InetAddress;

import org.icmp4j.IcmpPingResponse;
import org.icmp4j.IcmpPingRequest;
import org.icmp4j.IcmpPingUtil;
import org.icmp4j.platform.NativeBridge;
import org.icmp4j.platform.windows.jna.Winsock2Library;
import org.icmp4j.platform.windows.jna.LibraryUtil;
import org.icmp4j.platform.windows.jna.IcmpLibrary;

import com.sun.jna.Pointer;
import com.sun.jna.Memory;
import com.sun.jna.platform.win32.Kernel32;

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
 * Time: 9:00:08 PM
 */
public class WindowsNativeBridge extends NativeBridge {

  /**
   * The NativeBridge interface
   * Invoked to initialize this object
   */
  @Override
  public void initialize () {
    
    // delegate
    LibraryUtil.initialize ();

    // initialize winsock2
    final Winsock2Library winsock2Library = LibraryUtil.getWinsock2Library ();
    final Winsock2Library.WSAData wsadata = new Winsock2Library.WSAData ();
    if (winsock2Library.WSAStartup ((short) 2, wsadata) != 0 ){
      throw new RuntimeException ("WSAStartup failed");
    }
  }

  /**
   * The NativeBridge interface
   * Invoked to destroy this object
   */
  @Override
  public void destroy () {

    // destroy winsock2
    final Winsock2Library winsock2Library = LibraryUtil.getWinsock2Library ();
    if (winsock2Library != null) {
      winsock2Library.WSACleanup ();
    }
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
   * WARNING: this method is synchronized because if multiple threads call
   * IcmpLibrary.IcmpSendEcho (), then the results are corrupted!!!
   * So for now we have no choice other than figure out how to call IcmpSendEcho2,
   * or call ping.exe
   * 
   * @param request
   * @return IcmpEchoResponse
   */
  @Override
  public synchronized IcmpPingResponse executePingRequest (final IcmpPingRequest request) {

    // helpers
    final IcmpLibrary icmpLibrary = LibraryUtil.getIcmpLibrary ();
    final String host = request.getHost ();
    final int ttl = request.getTtl ();
    final int size = request.getPacketSize ();
    final int timeout = new Long (request.getTimeout ()).intValue ();

    // handle exceptions
    Pointer hIcmp = null;
    try {

      // request
      final InetAddress address = InetAddress.getByName (host);
      final byte[] ipAddressAsByteArray = address.getAddress ();

      final IcmpLibrary.IpAddrByVal ipaddr = new IcmpLibrary.IpAddrByVal ();
      ipaddr.bytes = ipAddressAsByteArray;

      final int replyDataSize = size + (new IcmpLibrary.IcmpEchoReply ().size ());
      final Pointer sendData  = new Memory (size);
      final Pointer replyData = new Memory (replyDataSize);
      final IcmpLibrary.IpOptionInformationByRef ipOption = new IcmpLibrary.IpOptionInformationByRef ();
      ipOption.ttl = (byte) ttl;
      ipOption.tos = (byte) 0;
      ipOption.flags = (byte) 0;
      ipOption.optionsSize = (byte) 0;
      ipOption.optionsData = null;

      // delegate
      hIcmp = icmpLibrary.IcmpCreateFile ();
      final long icmpSendEchoStartNanoTime = System.nanoTime ();
      final int returnCode = icmpLibrary.IcmpSendEcho (
        hIcmp,
        ipaddr,
        sendData,
        (short) size,
        ipOption,
        replyData,
        replyDataSize,
        timeout);
      final long icmpSendEchoNanoDuration = System.nanoTime () - icmpSendEchoStartNanoTime;
      final long icmpSendEchoDuration = icmpSendEchoNanoDuration / 1000 / 1000;
      
      // immediately check for timeout - whether the IcmpSendEcho call returns 0 or something else,
      // a last error of 11010 means that the call timed out, so return a proper response to indicate
      // that the call timed out
      final int lastError = Kernel32.INSTANCE.GetLastError ();
      if (lastError == 11010) {
        return IcmpPingUtil.createTimeoutIcmpPingResponse (icmpSendEchoDuration);
      }

      // check for failure
      if (returnCode == 0) {

        // call failed
        final String errorMessage = "Last error: " + lastError;

        // objectify
        final IcmpPingResponse response = new IcmpPingResponse ();
        response.setErrorMessage (errorMessage);
        response.setSuccessFlag (false);

        // done
        return response;
      }
      
      // the operation worked: check for success
      final IcmpLibrary.IcmpEchoReply icmpEchoReply = new IcmpLibrary.IcmpEchoReply (replyData);
      final boolean successFlag = icmpEchoReply.status == 0;
      final String responseAddress = String.format ("%d.%d.%d.%d",
        icmpEchoReply.address.bytes[0] & 0xff,
        icmpEchoReply.address.bytes[1] & 0xff,
        icmpEchoReply.address.bytes[2] & 0xff,
        icmpEchoReply.address.bytes[3] & 0xff
      );
      final String message = getStatusText (icmpEchoReply.status);

      // objectify
      final IcmpPingResponse response = new IcmpPingResponse ();
      response.setDuration (icmpSendEchoDuration);
      response.setHost (responseAddress);
      response.setErrorMessage (message);
      response.setRtt (icmpEchoReply.roundTripTime);
      response.setSize (icmpEchoReply.dataSize);
      response.setSuccessFlag (successFlag);
      response.setTtl (icmpEchoReply.options.ttl & 0xff);

      // done
      return response;
    }
    catch (final Exception e) {
      
      // propagate
      throw new RuntimeException (e);
    }
    finally {

      // cleanup
      if (hIcmp != null) {
        icmpLibrary.IcmpCloseHandle (hIcmp);
      }
    }
  }
  
  /**
   * Returns the text for the given status
   * @param status
   * @return String
   */
  private static String getStatusText (final int status) {
    switch (status){
      case 0 :     return "SUCCESS";
      case 11000 : return "IP_SUCCESS";
      case 11001 : return "IP_BUF_TOO_SMALL";
      case 11002 : return "IP_DEST_NET_UNREACHABLE";
      case 11003 : return "IP_DEST_HOST_UNREACHABLE";
      case 11004 : return "IP_DEST_PROT_UNREACHABLE";
      case 11005 : return "IP_DEST_PORT_UNREACHABLE";
      case 11006 : return "IP_NO_RESOURCES";
      case 11007 : return "IP_BAD_OPTION";
      case 11008 : return "IP_HW_ERROR";
      case 11009 : return "IP_PACKET_TOO_BIG";
      case 11010 : return "IP_REQ_TIMED_OUT";
      case 11011 : return "IP_BAD_REQ";
      case 11012 : return "IP_BAD_ROUTE";
      case 11013 : return "IP_TTL_EXPIRED_TRANSIT";
      case 11014 : return "IP_TTL_EXPIRED_REASSEM";
      case 11015 : return "IP_PARAM_PROBLEM";
      case 11016 : return "IP_SOURCE_QUENCH";
      case 11017 : return "IP_OPTION_TOO_BIG";
      case 11018 : return "IP_BAD_DESTINATION";
      case 11050 : return "IP_GENERAL_FAILURE";
    }
    return "UNKNOWN_STATUS";
  }
}