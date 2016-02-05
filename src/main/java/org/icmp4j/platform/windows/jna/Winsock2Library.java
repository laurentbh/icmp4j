package org.icmp4j.platform.windows.jna;

import java.util.List;
import java.util.Arrays;

import com.sun.jna.Library;
import com.sun.jna.Structure;
import com.sun.jna.Pointer;

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
 * Time: 6:14:50 PM
 */
public interface Winsock2Library extends Library {

  public static class WSAData extends Structure {
    public short version;
    public short highVersion;
    public byte[] description = new byte[256+1];
    public byte[] systemStatus = new byte[256+1];
    public short maxSockets;
    public short maxUdpDg;
    public Pointer vendorInfo;
    
    @Override
    protected List<String> getFieldOrder() {
      return Arrays.asList (new String[] {"version", "highVersion", "description", "systemStatus", "maxSockets", "maxUdpDg", "vendorInfo"});
    }
  }

  public static class Hostent extends Structure {
    public Pointer name;
    public Pointer aliases;
    public short addrType;
    public short length;
    public Pointer addressList;
    
    @Override
    protected List<String> getFieldOrder() {
      return Arrays.asList (new String[] {"name", "aliases", "addrType", "length", "addressList"});
    }
  }

  int WSAStartup(short versionRequested, WSAData wsadata);

  int WSACleanup();

  Hostent gethostbyname(String name);
}