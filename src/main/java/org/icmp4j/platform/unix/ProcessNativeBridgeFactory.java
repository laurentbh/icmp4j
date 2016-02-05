package org.icmp4j.platform.unix;

import org.icmp4j.constants.OsFamilyCode;
import org.icmp4j.platform.NativeBridge;
import org.icmp4j.util.PlatformUtil;

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
public class ProcessNativeBridgeFactory {
  
  public static NativeBridge createNativeBridge () {
    
    final int osFamilyCode = PlatformUtil.getOsFamilyCode ();
    if ( osFamilyCode == OsFamilyCode.MAC ) {
      return  new MacProcessNativeBridge ();
    } else {
      return new LinuxProcessNativeBridge ();
    }
  }
}
