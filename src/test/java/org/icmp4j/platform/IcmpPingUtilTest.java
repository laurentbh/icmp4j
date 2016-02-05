package org.icmp4j.platform;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.Random;
import java.util.LinkedList;

import junit.framework.TestCase;
import org.icmp4j.IcmpPingUtil;
import org.icmp4j.IcmpPingResponse;
import org.icmp4j.AsyncCallback;
import org.icmp4j.IcmpPingRequest;

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
 * Date: Oct 9, 2014
 * Time: 6:58:48 PM
 */
public class IcmpPingUtilTest extends TestCase {
  
  // my attributes
  private static final Random random = new Random ();
  private static final List<String> ipAddressList = new LinkedList<String> ();
  
  // static initializer
  static
  {
    ipAddressList.add ("www.google.com");
    ipAddressList.add ("www.google.org");
    ipAddressList.add ("www.microsoft.com");
    ipAddressList.add ("www.nasa.gov");
    ipAddressList.add ("www.openras.com");
    ipAddressList.add ("www.shortpasta.org");
    ipAddressList.add ("www.yahoo.com");
    ipAddressList.add ("www.yahoo.org");
    ipAddressList.add ("www.whitehouse.gov");
  }

  /**
   * Tests executePingRequest ()
   * @throws Exception
   */
  public void ___test_executePingRequest_www_google_com ()
    throws Exception {

    // helpers
    System.out.println ("test_executePingRequest_www_google_com ()");

    // response
    final CountDownLatch countDownLatch = new CountDownLatch (1);
    final AtomicBoolean successFlag = new AtomicBoolean ();
    final AsyncCallback<IcmpPingResponse> asyncCallback = new AsyncCallback<IcmpPingResponse> () {

      public void onSuccess (final IcmpPingResponse response) {

        // log
        System.out.println ("response: " + response);
        successFlag.set (true);

        // signal main thread
        countDownLatch.countDown ();
      }

      public void onFailure (final Throwable throwable) {
        
        // log
        throwable.printStackTrace ();
        
        // signal main thread
        countDownLatch.countDown ();
      }
    };
    
    // delegate
    executePingRequest ("www.google.com", asyncCallback);
    countDownLatch.await ();
    
    System.out.println ("test_executePingRequest_www_google_com (): DONE!");
  }
  
  /**
   * Tests executePingRequest ()
   * @throws Exception
   */
  public void test_executePingRequest_www_google_com_ttl ()
    throws Exception {
    
    // response
    final int maxTtl = 10;
    final CountDownLatch countDownLatch = new CountDownLatch (maxTtl);
    
    // start each
    final List<IcmpPingResponse> responseList = new LinkedList<IcmpPingResponse> ();
    for (int ttl = 1; ttl <= maxTtl; ttl ++) {
      
      // response
      final AsyncCallback<IcmpPingResponse> asyncCallback = new AsyncCallback<IcmpPingResponse> () {
  
        public void onSuccess (final IcmpPingResponse response) {
  
          // log
          System.out.println ("response: " + response);

          // track
          responseList.add (response);
  
          // signal main thread
          countDownLatch.countDown ();
        }
  
        public void onFailure (final Throwable throwable) {
          
          // log
          throwable.printStackTrace ();
          
          // signal main thread
          countDownLatch.countDown ();
        }
      };
      
      // request
      final IcmpPingRequest request = IcmpPingUtil.createIcmpPingRequest ();
      request.setHost ("www.google.com");
      request.setTtl (ttl);
      request.setTimeout (30000);

      // delegate
      System.out.println ("pinging " + request.getHost () + " with ttl: " + ttl);
      IcmpPingUtil.executePingRequest (request, asyncCallback);
    }
    
    // wait for all to finish
    System.out.println ("waiting for " + maxTtl + " threads to complete");
    countDownLatch.await ();
    System.out.println ("waiting for " + maxTtl + " threads to complete: done");
    System.out.println ("responseList: " + responseList.size () + " elements");
    
    // post-conditions: all should have worked
    assertEquals (
      maxTtl + " == " + responseList.size (),
      maxTtl,
      responseList.size ());
  }
  
  /**
   * Tests executePingRequest ()
   * @throws Exception
   */
  public void ___test_executePingRequest_parallel_5 ()
    throws Exception {
    
    // delegate
    final int count = 5;
    test_executePingRequest_parallel (count);
  }
  
  /**
   * Tests executePingRequest ()
   * @throws Exception
   */
  public void test_executePingRequest_parallel (final int count)
    throws Exception {

    // response
    final CountDownLatch countDownLatch = new CountDownLatch (count);
    final AtomicInteger successCount = new AtomicInteger ();
    final AsyncCallback<IcmpPingResponse> asyncCallback = new AsyncCallback<IcmpPingResponse> () {

      public void onSuccess (final IcmpPingResponse response) {

        // log
        System.out.println ("response: " + response);
        successCount.incrementAndGet ();

        // signal main thread
        countDownLatch.countDown ();
      }

      public void onFailure (final Throwable throwable) {

        // log
        throwable.printStackTrace ();
        
        // signal main thread
        countDownLatch.countDown ();
      }
    };
    
    // create 1 thread per
    for (int index = 0; index < count; index ++) {

      // delegate to the async method which executes the ping in the background
      final String ipAddress = nextRandomElement (ipAddressList);
      System.out.println ("starting thread " + index + " for ipAddress " + ipAddress);
      executePingRequest (ipAddress, asyncCallback);
    }
    
    // wait for all to finish
    System.out.println ("waiting for " + count + " threads to complete");
    countDownLatch.await ();
    System.out.println ("waiting for " + count + " threads to complete: done");
    System.out.println ("successCount: " + successCount.get ());
    
    // post-conditions: all should have worked
    assertEquals (
      count + " == " + successCount.get (),
      count,
      successCount.get ());
  }

  /**
   * Uniformly executes the given ping request
   * @param host
   * @param asyncCallback
   */
  private void executePingRequest (
    final String host,
    final AsyncCallback<IcmpPingResponse> asyncCallback) {

    // request
    final IcmpPingRequest request = IcmpPingUtil.createIcmpPingRequest ();
    request.setHost (host);
    
    // delegate
    IcmpPingUtil.executePingRequest (request, asyncCallback);
  }
  
  /**
   * Returns the next random element from the given Collection
   * @return list
   */
  private static <T> T nextRandomElement (final List<T> list) {

    // preconditions: array cannot be empty, otherwise we would have to return null, in which
    // case the caller would not be able to tell whether the element returned is a valid element or not
    if (list.size () == 0) {
      throw new RuntimeException ("list.size () must be > 0");
    }

    // randomize the next
    final int lowerBound = 0;
    final int upperBound = list.size ();
    final int index = lowerBound + random.nextInt (upperBound - lowerBound);
    return list.get (index);
  }

  /**
   * The main entry point for intellij
   * @param args
   */
  public static void main (final String[] args) {

    // handle exceptions
    try {

      // delegate
      final IcmpPingUtilTest test = new IcmpPingUtilTest ();
      test.setUp ();
      // TODO: test.test_executePingRequest_www_google_com_ttl ();
    }
    catch (final Throwable t) {

      // log
      t.printStackTrace ();
    }
  }
}