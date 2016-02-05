package org.icmp4j.util;

import java.util.Map;
import java.util.HashMap;
import java.io.File;

import com.sun.jna.Native;

import org.icmp4j.logger.Logger;

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
 * Date: Feb 4, 2014
 * Time: 5:02:49 PM
 */
public class JnaUtil {

  // my attributes
  private static final Logger logger = Logger.getLogger (JnaUtil.class);
  private static final Map<String, Object> libraryMap = new HashMap<String, Object> ();

  /**
   * Loads the given library
   * Returns the library
   * 
   * This method loads the library natively via Native.loadLibrary ().
   * If that fails, this method looks for the library as a resource (meaning inside jars, wars, ...),
   * extracts it, and then loads it.
   * If that fails as well, this throws a RuntimeException 
   * 
   * @param libraryName
   * @param libraryClass
   * @return Object
   */
  public static Object loadLibrary (
    final String libraryName,
    final Class libraryClass) {

    // already loaded?
    synchronized (libraryMap) {

      final Object library = libraryMap.get (libraryName);
      if (library != null) {
        return library;
      }
    }

    // the library has not yet been loaded: load it
    // support concurrency
    synchronized (JnaUtil.class) {

      // try to load from the java.library.path
      Object library;
      {
        final String strategy = "native via java.library.path";
        library = loadLibraryNoException (strategy, libraryName, libraryClass);
      }

      if (library == null) {
        final String resourceName = ResourceUtil.buildLibraryName(libraryName);
        final File resourceFile = ResourceUtil.findResourceAsFile (resourceName);
        if (resourceFile != null) {
          final String strategy = "native via resource lookup";
          final String path = resourceFile.getAbsolutePath ();
          library = loadLibraryNoException (strategy, path, libraryClass);
        }
      }

      if (library == null) {
    	final String resourceName = ResourceUtil.buildLibraryName(libraryName);
        final String strategy = "as-resource";
        final File file = SystemUtil.extractLibraryByResource (resourceName);
        final String path = file.getAbsolutePath ();
        library = loadLibraryNoException (strategy, path, libraryClass);
      }

      if (library == null) {
        throw new RuntimeException ("Failed to load library ");
      }


      // track with the original libraryName!
      synchronized (libraryMap) {
        libraryMap.put (libraryName, library);
      }
    }

    // recurse to ensure proper caching
    return loadLibrary (
      libraryName,
      libraryClass);
  }
  public static Object loadLibraryBestEffort (final String libraryName, final Class libraryClass) {
	  try {
		  return loadLibrary(libraryName, libraryClass);
	  } catch (RuntimeException e) {
		  logger.warn(e.getMessage());
		  return null;
	  }
  }

  /**
   * Uniformly loads the given library
   * @param strategy
   * @param libraryName
   * @param libraryClass
   * @return Object
   */
  private static Object loadLibraryNoException (
    final String strategy,
    final String libraryName,
    final Class libraryClass) {

    // handle exceptions
    try {

      // try to load from the java.library.path
      logger.info ("loadLibrary2 ()");
      logger.info ("  strategy: " + strategy);
      logger.info ("  libraryName: " + libraryName);
      logger.info ("  libraryClass: " + libraryClass.getName ());
      return Native.loadLibrary (libraryName, libraryClass);
    }
    catch (final Throwable t) {

      // log
      logger.warn (
        "loadLibraryNoException (). Native.loadLibrary () failed. " +
        "exception " + t.getClass ().getName () + ", " +
        "errorMessage: " + ExceptionUtil.getMessage (t));

      // done
      return null;
    }
  }
}