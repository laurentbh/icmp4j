package org.icmp4j;

import java.util.concurrent.CountDownLatch;
import java.util.TreeMap;

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
 * Date: Oct 10, 2014
 * Time: 12:40:53 AM
 */
public class IcmpTraceRouteUtil {

  /**
   * This is what a tracert looks like from the command line:
   * C:\Users\sal>tracert www.google.com
   * Tracing route to www.google.com [74.125.224.52]
   * over a maximum of 30 hops:
   *   1     7 ms     9 ms     1 ms  10.142.222.1
   *   2    10 ms     5 ms     3 ms  10.0.0.1
   *   3    12 ms    10 ms     9 ms  10.6.44.1
   *   4    28 ms    14 ms    15 ms  ip68-4-12-22.oc.oc.cox.net [68.4.12.22]
   *   5    24 ms    30 ms    98 ms  ip68-4-11-98.oc.oc.cox.net [68.4.11.98]
   *   6    22 ms    19 ms    19 ms  68.1.5.137
   *   7    18 ms    19 ms    19 ms  langbbrj01-ge050000804.r2.la.cox.net [68.105.30.181]
   *   8    17 ms    29 ms    21 ms  216.239.46.40
   *   9    22 ms    27 ms    20 ms  209.85.252.149
   *  10    28 ms    19 ms    27 ms  lax17s01-in-f20.1e100.net [74.125.224.52]
   * Trace complete.
   * 
   * This is what a tracert looks like with this library:
   *     [junit] response: [
   *     [junit] ttl: 1, response: [hashCode: 15186923, successFlag: false, timeoutFlag: false, errorMessage: IP_TTL_EXPIRED_TRANSIT, throwable: null, host: 10.142.222.1, size: 0, rtt: 3, ttl: 64]
   *     [junit] ttl: 2, response: [hashCode: 31615954, successFlag: false, timeoutFlag: false, errorMessage: IP_TTL_EXPIRED_TRANSIT, throwable: null, host: 10.0.0.1, size: 0, rtt: 13, ttl: 63]
   *     [junit] ttl: 3, response: [hashCode: 1367891, successFlag: false, timeoutFlag: false, errorMessage: IP_TTL_EXPIRED_TRANSIT, throwable: null, host: 10.6.44.1, size: 0, rtt: 23, ttl: 253]
   *     [junit] ttl: 4, response: [hashCode: 5370470, successFlag: false, timeoutFlag: false, errorMessage: IP_TTL_EXPIRED_TRANSIT, throwable: null, host: 68.4.12.20, size: 0, rtt: 23, ttl: 252]
   *     [junit] ttl: 5, response: [hashCode: 73029, successFlag: false, timeoutFlag: false, errorMessage: IP_TTL_EXPIRED_TRANSIT, throwable: null, host: 68.4.11.96, size: 0, rtt: 27, ttl: 251]
   *     [junit] ttl: 6, response: [hashCode: 19446204, successFlag: false, timeoutFlag: false, errorMessage: IP_TTL_EXPIRED_TRANSIT, throwable: null, host: 68.1.0.136, size: 0, rtt: 33, ttl: 247]
   *     [junit] ttl: 7, response: [hashCode: 12193604, successFlag: false, timeoutFlag: false, errorMessage: IP_TTL_EXPIRED_TRANSIT, throwable: null, host: 68.105.30.181, size: 0, rtt: 33, ttl: 247]
   *     [junit] ttl: 8, response: [hashCode: 20995753, successFlag: false, timeoutFlag: false, errorMessage: IP_TTL_EXPIRED_TRANSIT, throwable: null, host: 64.233.174.238, size: 0, rtt: 29, ttl: 248]
   *     [junit] ttl: 9, response: [hashCode: 17219963, successFlag: false, timeoutFlag: false, errorMessage: IP_TTL_EXPIRED_TRANSIT, throwable: null, host: 72.14.236.13, size: 0, rtt: 33, ttl: 247]
   *     [junit] ttl: 10, response: [hashCode: 8947790, successFlag: true, timeoutFlag: false, errorMessage: SUCCESS, throwable: null, host: 74.125.224.212, size: 32, rtt: 28, ttl: 55]
   *     [junit] ttl: 11, response: [hashCode: 28106261, successFlag: true, timeoutFlag: false, errorMessage: SUCCESS, throwable: null, host: 74.125.224.212, size: 32, rtt: 31, ttl: 55]
   *     [junit] ttl: 12, response: [hashCode: 2651170, successFlag: true, timeoutFlag: false, errorMessage: SUCCESS, throwable: null, host: 74.125.224.212, size: 32, rtt: 30, ttl: 55]
   *     [junit] ttl: 13, response: [hashCode: 31485310, successFlag: true, timeoutFlag: false, errorMessage: SUCCESS, throwable: null, host: 74.125.224.212, size: 32, rtt: 29, ttl: 55]
   *     [junit] ttl: 14, response: [hashCode: 20216452, successFlag: true, timeoutFlag: false, errorMessage: SUCCESS, throwable: null, host: 74.125.224.212, size: 32, rtt: 31, ttl: 55]
   *     [junit] ttl: 15, response: [hashCode: 5746246, successFlag: true, timeoutFlag: false, errorMessage: SUCCESS, throwable: null, host: 74.125.224.212, size: 32, rtt: 17, ttl: 55]
   *     [junit] ttl: 16, response: [hashCode: 7514401, successFlag: true, timeoutFlag: false, errorMessage: SUCCESS, throwable: null, host: 74.125.224.212, size: 32, rtt: 32, ttl: 55]
   *     ...
   *     [junit] ttl: 29, response: [hashCode: 29892897, successFlag: true, timeoutFlag: false, errorMessage: SUCCESS, throwable: null, host: 74.125.224.212, size: 32, rtt: 29, ttl: 55]
   *     [junit] ttl: 30, response: [hashCode: 32973925, successFlag: true, timeoutFlag: false, errorMessage: SUCCESS, throwable: null, host: 74.125.224.212, size: 32, rtt: 29, ttl: 55]
   *     [junit] ]
   */

  /**
   * Executes the given TraceRoute request
   * @param request
   * @return IcmpTraceRouteResponse
   */
  public static IcmpTraceRouteResponse executeTraceRoute (final IcmpTraceRouteRequest request) {

    // request
    final int maxTtl;
    {
      final int requestTtl = request.getTtl ();
      maxTtl = requestTtl > 0 ?
        requestTtl :
        30;
    }
    final CountDownLatch countDownLatch = new CountDownLatch (maxTtl);

    // start one ping request for each TTL
    // note that the first 10 requests are expected to timeout with an error code of IP_TTL_EXPIRED_TRANSIT
    // because it takes more than 10 hops to get to google.com
    // track responses by ttl
    final TreeMap<Integer, IcmpPingResponse> ttlToResponseMap = new TreeMap<Integer, IcmpPingResponse> ();
    for (int ttl = 1; ttl <= maxTtl; ttl ++) {

      // response
      final int finalTtl = ttl;
      final AsyncCallback<IcmpPingResponse> asyncCallback = new AsyncCallback<IcmpPingResponse> () {

        public void onSuccess (final IcmpPingResponse response) {

          // track
          synchronized (ttlToResponseMap) {
            ttlToResponseMap.put (finalTtl, response);
          }

          // signal main thread
          countDownLatch.countDown ();
        }

        public void onFailure (final Throwable throwable) {

          // objectify
          final IcmpPingResponse response = new IcmpPingResponse ();
          response.setSuccessFlag (false);
          response.setThrowable (throwable);

          // track
          synchronized (ttlToResponseMap) {
            ttlToResponseMap.put (finalTtl, response);
          }

          // signal main thread
          countDownLatch.countDown ();
        }
      };

      // request
      final IcmpPingRequest icmpPingRequest = IcmpPingUtil.createIcmpPingRequest ();
      icmpPingRequest.setHost (request.getHost ());
      icmpPingRequest.setPacketSize (request.getPacketSize ());
      icmpPingRequest.setTtl (ttl);
      icmpPingRequest.setTimeout (request.getTimeout ());

      // delegate
      IcmpPingUtil.executePingRequest (icmpPingRequest, asyncCallback);
    }

    // handle exceptions
    try {

      // wait for all to finish
      countDownLatch.await ();

      // response
      final IcmpTraceRouteResponse response = new IcmpTraceRouteResponse ();
      response.setTtlToResponseMap (ttlToResponseMap);

      // done
      return response;
    }
    catch (final InterruptedException e) {

      // propagate
      Thread.currentThread ().interrupt ();
      throw new RuntimeException (e);
    }
  }
}