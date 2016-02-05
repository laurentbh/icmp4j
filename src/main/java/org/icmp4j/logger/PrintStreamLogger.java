package org.icmp4j.logger;

import java.io.PrintStream;

import org.icmp4j.logger.constants.LogLevel;
import org.icmp4j.logger.constants.LogLevelName;

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
 * Date: Nov 22, 2009
 * Time: 10:36:13 AM
 */
public class PrintStreamLogger extends LoggerAdapter {

  // my attributes
  private static int loglevel = LogLevel.DEBUG;
  private PrintStream ps;

  // my attributes
  public static void setLogLevel (final String logLevelString) {
    if (logLevelString.equalsIgnoreCase (LogLevelName.TRACE)) {
      loglevel = LogLevel.TRACE;
    }
    else if (logLevelString.equalsIgnoreCase (LogLevelName.DEBUG)) {
      loglevel = LogLevel.DEBUG;
    }
    else if (logLevelString.equalsIgnoreCase (LogLevelName.INFO)) {
      loglevel = LogLevel.INFO;
    }
    else if (logLevelString.equalsIgnoreCase (LogLevelName.ERROR)) {
      loglevel = LogLevel.ERROR;
    }
    else if (logLevelString.equalsIgnoreCase (LogLevelName.WARN)) {
      loglevel = LogLevel.WARN;
    }
    else if (logLevelString.equalsIgnoreCase (LogLevelName.FATAL)) {
      loglevel = LogLevel.FATAL;
    }
  }

  /**
   * Def ctor
   * Needed so that Logger can instantiate this via PrintStreamLogger.class.newInstance!
   */
  public PrintStreamLogger () {
    this (System.out);
  }

  /**
   * Def ctor
   * @param ps
   */
  public PrintStreamLogger (final PrintStream ps) {
    this.ps = ps;
  }

  @Override
  public boolean isDebugEnabled () { return LogLevel.DEBUG >= loglevel; }
  @Override
  public boolean isInfoEnabled () { return LogLevel.INFO >= loglevel; }
  @Override
  public boolean isTraceEnabled () { return LogLevel.TRACE >= loglevel;}

  /**
   * The Logger interface
   * Invoked to log the given message
   * @param logLevel
   * @param object
   * @param t
   * @noinspection LoopStatementThatDoesntLoop,ConstantConditions
   */
  @Override
  protected void log2 (
    final int logLevel,
    final Object object,
    final Throwable t) {

    // discriminate by logLevel
    if (logLevel < PrintStreamLogger.loglevel) {
      return;
    }

    // propagate
    super.log2 (
      logLevel,
      object,
      t);
  }

  /**
   * The LoggerAdapter interface
   * Invoked to log the given message
   * @param logLevel
   * @param line
   */
  protected void printLine (
    final int logLevel,
    final String line) {

    // 
    ps.println (line);
  }
}