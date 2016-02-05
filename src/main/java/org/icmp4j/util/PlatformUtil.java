package org.icmp4j.util;

import org.icmp4j.constants.OsFamilyCode;

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
 * Time: 10:47:09 PM
 */
public class PlatformUtil {

  // my attributes
  private static int osFamilyCode = OsFamilyCode.ZERO;

  // my attributes
  public static void setOsFamilyCode (final int osFamilyCode) { PlatformUtil.osFamilyCode = osFamilyCode; }
  public static int getOsFamilyCode () { return osFamilyCode; }

  /**
   * Initializes this component
   */
  public static void initialize () {

    // detect the OS type
    // according to http://mindprod.com/jgloss/properties.html
    // Here are the values Sun uses for the os.name property to identify various platforms:
    //   os.name
    //   AIX
    //   Digital Unix
    //   FreeBSD
    //   HP UX
    //   Irix
    //   Linux
    //   Mac OS
    //   Mac OS X 
    //   MPE/iX
    //   Netware 4.11
    //   OS/2
    //   Solaris
    //   Windows 2000
    //   Windows 95
    //   Windows 98
    //   Windows NT
    //   Windows Vista
    //   Windows XP
    //
    // on Dekker's Android 2.3.3 (Samsung S6802):
    //  final String osName = System.getProperty ("os.name")
    // outputs
    //   Android 2.3.3
    if (osFamilyCode == OsFamilyCode.ZERO) {

      // osName-specific processing
      final String osName = System.getProperty ("os.name");
      // System.out.println ("<PlatformUtil> <osName: " + osName + ">");
      final int osCode;
      if (osName.equals ("Android")) {
        osCode = OsFamilyCode.ANDROID;
      }
      else if (osName.equals ("Linux")) {
        osCode = OsFamilyCode.LINUX;
      }
      else if (osName.startsWith ("Mac OS")) {
        osCode = OsFamilyCode.MAC;
      }
      else if (osName.startsWith ("Windows")) {
        osCode = OsFamilyCode.WINDOWS;
      }
      else {
        osCode = OsFamilyCode.ZERO;
      }
      setOsFamilyCode (osCode);
    }
    else {

      // log for now
      System.out.println ("<PlatformUtil> <osFamilyCode already set to " + osFamilyCode + ">");
    }
  }
}