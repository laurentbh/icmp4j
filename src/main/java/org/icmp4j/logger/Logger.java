package org.icmp4j.logger;

import java.util.Map;
import java.util.HashMap;

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
 * Date: Jan 27, 2016
 * Time: 10:12:06 PM
 */
public abstract class Logger {

  // my attributes
  private static Class<? extends Logger> loggerClass;
  private static final Map<Object, Logger> categoryToLoggerMap = new HashMap<Object, Logger> ();
  private Object category;

  // my attributes
  public static void setLoggerClass (final Class<? extends Logger> loggerClass) {
    Logger.loggerClass = loggerClass;
  }

  public void setCategory (final Object category) { this.category = category; }
  public Object getCategory () { return category; }

  /**
   * 
   * @param category
   * @return Logger
   */
  public static Logger getLogger (final Object category) {

    // already cached?
    {
      final Logger logger = categoryToLoggerMap.get (category);
      if (logger != null) {
        return logger;
      }
    }

    // not cached: cache it
    synchronized (Logger.class) {

      // already cached?
      {
        final Logger logger = categoryToLoggerMap.get (category);
        if (logger != null) {
          return logger;
        }
      }

      // the specified category does not have an associated Logger: instantiate the Logger
      // handle exceptions
      Logger logger;
      try {

        // if you specified loggerClass
        if (loggerClass != null) {
          logger = loggerClass.newInstance ();
        }
        else {
          logger = new PrintStreamLogger (System.out);
        }
      }
      catch (final Exception e) {

        // log
        System.out.println ("<FATAL> <>");
        e.printStackTrace ();

        // creating a specifing Logger failed, resort to good-old System.out calls
        logger = new PrintStreamLogger (System.out);
      }

      // uniformly initialize the Logger
      logger.setCategory (category);

      // cache & recurse to ensure proper caching
      categoryToLoggerMap.put (category, logger);
      return getLogger (category);
    }
  }

  /**
   * The Logger interface
   * @param object
   */
  public void trace (final Object object) {

    // delegate
    final Throwable t = null;
    log (LogLevel.TRACE, object, t);
  }

  /**
   * The Logger interface
   * @param object
   * @param t
   */
  public void trace (final Object object, final Throwable t) {

    // delegate
    log (LogLevel.TRACE, object, t);
  }

  /**
   * The Logger interface
   * @param t
   */
  public void trace (final Throwable t) {

    // delegate
    final Object object = null;
    log (LogLevel.TRACE, object, t);
  }

  /**
   * The Logger interface
   * @param object
   */
  public void debug (final Object object) {

    // delegate
    final Throwable t = null;
    log (LogLevel.DEBUG, object, t);
  }

  /**
   * The Logger interface
   * @param object
   * @param t
   */
  public void debug (final Object object, final Throwable t) {

    // delegate
    log (LogLevel.DEBUG, object, t);
  }

  /**
   * The Logger interface
   * @param t
   */
  public void debug (final Throwable t) {

    // delegate
    final Object object = null;
    log (LogLevel.DEBUG, object, t);
  }

  /**
   * @param object
   */
  public void info (final Object object) {

    // delegate
    final Throwable t = null;
    log (LogLevel.INFO, object, t);
  }

  /**
   * @param t
   */
  public void info (final Throwable t) {

    // delegate
    final Object object = null;
    log (LogLevel.INFO, object, t);
  }

  /**
   * The Logger interface
   * @param object
   */
  public void warn (final Object object) {

    // delegate
    final Throwable t = null;
    log (LogLevel.WARN, object, t);
  }

  /**
   * The Logger interface
   * @param object
   * @param t
   */
  public void warn (final Object object, final Throwable t) {

    // delegate
    log (LogLevel.WARN, object, t);
  }

  /**
   * The Logger interface
   * @param t
   */
  public void warn (final Throwable t) {

    // delegate
    final Object object = null;
    log (LogLevel.WARN, object, t);
  }

  /**
   * The Logger interface
   * @param object
   */
  public void error (final Object object) {

    // delegate
    final Throwable t = null;
    log (LogLevel.ERROR, object, t);
  }

  /**
   * The Logger interface
   * @param object
   * @param t
   */
  public void error (final Object object, final Throwable t) {

    // delegate
    log (LogLevel.ERROR, object, t);
  }

  /**
   * The Logger interface
   * @param t
   */
  public void error (final Throwable t) {

    // delegate
    final Object object = null;
    log (LogLevel.ERROR, object, t);
  }

  /**
   * The Logger interface
   * @param object
   */
  public void fatal (final Object object) {

    // delegate
    final Throwable t = null;
    log (LogLevel.FATAL, object, t);
  }

  /**
   * The Logger interface
   * @param object
   * @param t
   */
  public void fatal (final Object object, final Throwable t) {

    // delegate
    log (LogLevel.FATAL, object, t);
  }

  /**
   * The Logger interface
   * @param t
   */
  public void fatal (final Throwable t) {

    // delegate
    final Object object = null;
    log (LogLevel.FATAL, object, t);
  }

  /**
   * The Logger interface
   * Invoked to log the given object
   * @param logLevel
   * @param object
   * @param t
   * @noinspection LoopStatementThatDoesntLoop
   */
  protected void log (
    final int logLevel,
    final Object object,
    final Throwable t) {

    // delegate
    log2 (
      logLevel,
      object,
      t);
  }

  /**
   * The Logger interface
   * Invoked to log the given object
   * @param logLevel
   * @param object
   * @param t
   */
  protected void log2 (
    final int logLevel,
    final Object object,
    final Throwable t) {

  }

  /**
   * The Logger interface
   * Invoked to determine if the given log level is enabled
   * @return boolean
   */
  public abstract boolean isDebugEnabled ();

  /**
   * The Logger interface
   * Invoked to determine if the given log level is enabled
   * @return boolean
   */
  public abstract boolean isInfoEnabled ();

  /**
   * The Logger interface
   * Invoked to determine if the given log level is enabled
   * @return boolean
   */
  public abstract boolean isTraceEnabled ();

  /**
   * Returns the log level text for the given logLevel
   * @param logLevel
   * @return String
   */
  public static String getLogLevelText (final int logLevel) {

    // translate logLevel
    return
      logLevel == LogLevel.FATAL ? LogLevelName.FATAL :
      logLevel == LogLevel.ERROR ? LogLevelName.ERROR :
      logLevel == LogLevel.WARN  ? LogLevelName.WARN :
      logLevel == LogLevel.INFO  ? LogLevelName.INFO :
      logLevel == LogLevel.DEBUG ? LogLevelName.DEBUG :
      logLevel == LogLevel.TRACE ? LogLevelName.TRACE :
      String.valueOf (logLevel);
  }
}