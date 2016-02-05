package org.icmp4j;

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
 * Time: 7:08:09 PM
 */
public class IcmpPingResponse {

  // my attributes
  private boolean successFlag;
  private boolean timeoutFlag;
  private String errorMessage;
  private Throwable throwable;
  private String host;
  private int size;
  private int rtt;
  private int ttl;
  private long duration;
  
  // my attributes
  public void setSuccessFlag (final boolean successFlag) { this.successFlag = successFlag; }
  public boolean getSuccessFlag () { return successFlag; }
  
  public void setTimeoutFlag (final boolean timeoutFlag) { this.timeoutFlag = timeoutFlag; }
  public boolean getTimeoutFlag () { return timeoutFlag; }
  
  public void setErrorMessage (final String errorMessage) { this.errorMessage = errorMessage; }
  public String getErrorMessage () { return errorMessage; }
  
  public void setThrowable (final Throwable throwable) { this.throwable = throwable; }
  public Throwable getThrowable () { return throwable; }
  
  public void setHost (final String host) { this.host = host; }
  public String getHost () { return host; }
  
  public void setSize (final int size) { this.size = size; }
  public int getSize () { return size; }
  
  public void setRtt (final int rtt) { this.rtt = rtt; }
  public int getRtt () { return rtt; }
  
  public void setTtl (final int ttl) { this.ttl = ttl; }
  public int getTtl () { return ttl; }
  
  public void setDuration (final long duration) { this.duration = duration; }
  public long getDuration () { return duration; }

  /**
   * The Object interface
   * @return String
   */
  @Override
  public String toString () {
    
    return
      "[" +
      "hashCode: " + super.hashCode () + ", " +
      "successFlag: " + successFlag + ", " +
      "timeoutFlag: " + timeoutFlag + ", " +
      "errorMessage: " + errorMessage + ", " +
      "throwable: " + throwable + ", " +
      "host: " + host + ", " +
      "size: " + size + ", " +
      "rtt: " + rtt + ", " +
      "ttl: " + ttl + ", " +
      "duration: " + duration +
      "]";
  }
}