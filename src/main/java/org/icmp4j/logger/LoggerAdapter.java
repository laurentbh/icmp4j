package org.icmp4j.logger;

import java.util.List;
import java.util.LinkedList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.StringWriter;
import java.io.PrintWriter;

import org.icmp4j.logger.constants.LogLevel;

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
 * Date: Jan 21, 2010
 * Time: 9:37:23 AM
 */
public class LoggerAdapter extends Logger {

  // my constants
  public static final String DEBUG = "DEBUG";
  public static final String INFO = "INFO";
  public static final String ERROR = "ERROR";
  public static final String FATAL = "FATAL";
  public static final String WARN = "WARN";
  public static final String TRACE = "TRACE";

  // my static attributes
  private static int logLevel = LogLevel.DEBUG;
  private static final List<Logger> loggerList = new LinkedList<Logger> ();

  // my static attributes
  public static void addLogger (final Logger logger) {
    synchronized (LoggerAdapter.loggerList) {
      LoggerAdapter.loggerList.add (logger);
    }
  }
  public static void removeLogger (final Logger logger) {
    synchronized (LoggerAdapter.loggerList) {
      LoggerAdapter.loggerList.remove (logger);
    }
  }

  /**
   * Explicitly set the default log level.
   * @param logLevelString
   */
  public static void setLoglevel (final String logLevelString) {
    logLevel =
      logLevelString.equalsIgnoreCase (TRACE) ? LogLevel.TRACE :
      logLevelString.equalsIgnoreCase (DEBUG) ? LogLevel.DEBUG :
      logLevelString.equalsIgnoreCase (INFO) ? LogLevel.INFO :
      logLevelString.equalsIgnoreCase (ERROR) ? LogLevel.ERROR :
      logLevelString.equalsIgnoreCase (WARN) ? LogLevel.WARN :
      logLevelString.equalsIgnoreCase (FATAL) ? LogLevel.FATAL :
      logLevel;
  }

  // my attributes
  private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
  private Object category;

  // my attributes
  @Override
  public void setCategory (final Object category) { this.category = category; }
  public Object getCategory () { return category; }

  /**
   * Def ctor
   */
  public LoggerAdapter () {
  }

  /**
   * Def ctor
   * @param category
   */
  public LoggerAdapter (final Object category) {
    this.category = category;
  }

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

    // handle exceptions
    try {

      // generate the log line
      final StringBuilder sb = new StringBuilder ();

      // <2010-01-21 09:34:00>
      sb.append ('<');
      sb.append (simpleDateFormat.format (new Date ()));
      sb.append ('>');

      // <2010-01-21 09:34:00> <INFO>
      sb.append (" <");
      sb.append (getLogLevelText (logLevel));
      sb.append ('>');

      // <2010-01-21 09:34:00> <INFO> <Category>
      final String categoryName = category instanceof Class ?
        ((Class<?>) category).getSimpleName () :
        category.toString ();
      sb.append (" <");
      sb.append (categoryName);
      sb.append ('>');

      // <2010-01-21 09:34:00> <INFO> <Category> <userMessage
      sb.append (" <");

      if (object != null && t == null) {
        sb.append (object);
      }
      else if (object == null && t != null) {
        sb.append (getStackTrace (t));
      }
      else if (object != null && t != null) {
        sb.append (object);
        sb.append ("\n\r");
        sb.append (getStackTrace (t));
      }
      sb.append ('>');

      // delegate
      synchronized (LoggerAdapter.loggerList) {
        for (final Logger logger : LoggerAdapter.loggerList) {
          logger.log2 (logLevel, object, t);
        }
      }

      // delegate
      final String line = sb.toString ();
      printLine (logLevel, line);
    }
    catch (final Exception e) {

      // just print it
      final String string = object == null ?
        "null" :
        object.toString ();
      printLine (logLevel, string);
    }
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

  }

  @Override
  public boolean isDebugEnabled () { return LogLevel.DEBUG >= logLevel; }
  @Override
  public boolean isInfoEnabled () { return LogLevel.INFO >= logLevel; }
  @Override
  public boolean isTraceEnabled () { return LogLevel.TRACE >= logLevel;}

  /**
   * Helper to minimize dependencies
   * @param t
   * @return String
   */
  protected static String getStackTrace (final Throwable t) {

    final StringWriter sw = new StringWriter ();
    final PrintWriter pw = new PrintWriter (sw);
    t.printStackTrace (pw);
    return sw.toString ();
  }
}