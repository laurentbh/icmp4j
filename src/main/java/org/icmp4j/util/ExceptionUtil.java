package org.icmp4j.util;

/**
 * ShortPasta Foundation
 * http://www.shortpasta.org
 * Copyright 2009 and beyond, Sal Ingrilli at the ShortPasta Software Foundation
 * <p/>
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation as long as:
 * 1. You credit the original author somewhere within your product or website
 * 2. The credit is easily reachable and not burried deep
 * 3. Your end-user can easily see it
 * 4. You register your name (optional) and company/group/org name (required)
 * at http://www.shortpasta.org
 * 5. You do all of the above within 4 weeks of integrating this software
 * 6. You contribute feedback, fixes, and requests for features
 * <p/>
 * If/when you derive a commercial gain from using this software
 * please donate at http://www.shortpasta.org
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
 * Date: Feb 14, 2012
 * Time: 4:22:15 AM
 */
public class ExceptionUtil {
  
  /**
   * Returns the first available message for the given exception
   * @param t
   * @return String
   */
  public static String getMessage (final Throwable t) {

    // lookup the message in this class and all nested classes
    {
      final String message = lookupMessage (t);
      if (!StringUtil.isSameAsEmpty (message)) {
        return message;
      }
    }
    
    // message not found: default to something
    return t.getClass ().getName ();
  }
  
  /**
   * Looks for the message in the given exception
   * If not found, it looks in the next nested exception, recursively
   * If not found, returns null 
   * @param t
   * @return String
   */
  private static String lookupMessage (final Throwable t) {
    
    // lookup message
    {
      final String message = t.getMessage ();
      if (!StringUtil.isSameAsEmpty (message)) {
        return message;
      }
    }
    
    // message not found: lookup message in the nested exception
    {
      final Throwable cause = t.getCause ();
      if (cause != null) {
        
        final String message = getMessage (cause);
        if (!StringUtil.isSameAsEmpty (message)) {
          return message;
        }
      }
    }
    
    // message not found anywhere
    return null;
  }
}