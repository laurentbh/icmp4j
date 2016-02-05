package org.icmp4j.util;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.icmp4j.logger.Logger;
import org.icmp4j.exception.AssertRuntimeException;

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
 * Date: Jan 28, 2016
 * Time: 3:13:27 PM
 */
public class SystemUtil {

  // my variables
  private static final Logger logger = Logger.getLogger (SystemUtil.class);
  private static final AtomicInteger nextLibraryId = new AtomicInteger ();

  /**
   * Looks for the given library as a resource, meaning on the file system AND jars.
   * When found, the library is extracted to the temp directory.
   * For example, in Windows, when running as an applet:
   *   <DEBUG> <SystemUtil> <creating temp library: C:/Users/sal/shortpasta/loadLibrary-Rockey2/2014-02-04_08-11-03_1.dll>
   * Returns a File representing the extracted library
   * @param libraryName
   * @return File
   */
  public static File extractLibraryByResource (final String libraryName) {

    // load the library from within the jar
    final InputStream inputStream = findLibraryAsStream (libraryName);
    if (inputStream == null) {
      throw new AssertRuntimeException ("resource not found: " + libraryName);
    }

    // create a managed temp directory where to save the library.
    // this is simply a standard and easy way for us to find it.
    // when creating the library file, tell Java to delete it on exit so we do not have to deal with cleanup
    final File appHomeDirectory = getAppHomeDirectoryDirectory ();
    final File loadLibraryDirectory = new File (appHomeDirectory, "loadLibrary-" + libraryName);
    if (!loadLibraryDirectory.exists ()) {
      logger.debug ("creating temp directory: " + loadLibraryDirectory.getAbsolutePath ());
      if (!loadLibraryDirectory.mkdirs ()) {
        throw new AssertRuntimeException ("failed to create loadLibraryDirectory: " + loadLibraryDirectory.getAbsolutePath ());
      }
    }
    final int libraryId = nextLibraryId.incrementAndGet ();
    final String uniqueLibraryName =
      TimeUtil.formatDateAsFileSystemName () +
      "_" + String.valueOf (libraryId);
    final File uniqueLibraryFile = new File (loadLibraryDirectory, uniqueLibraryName);
    uniqueLibraryFile.deleteOnExit ();

    // save to the users's temp directory
    final String uniqueLibraryPath = uniqueLibraryFile.getAbsolutePath ();
    logger.debug ("creating temp library: " + uniqueLibraryPath);
    FileUtil.writeFile (
      uniqueLibraryFile,
      inputStream);

    // don
    return uniqueLibraryFile;
  }

  /**
   * Helper: uniformly looks for the given library within this jar
   * Returns null if not found 
   * @param libraryName
   * @return InputStream
   */
  private static InputStream findLibraryAsStream (final String libraryName) {

    final String resourcePath = "/" + libraryName;
    final InputStream inputStream = SystemUtil.class.getResourceAsStream (resourcePath);
    final String message = inputStream == null ?
      "findResourceAsStream (): resource " + resourcePath + " not found" :
      "findResourceAsStream (): resource " + resourcePath + " found";
    logger.debug (message);
    return inputStream;
  }
  
  /**
   * Returns the "icmp4j" directory located in "user.home".
   * On XP Pro: C:\Documents and Settings\Sal\icmp4j
   * @return File
   */
  private static File getAppHomeDirectoryDirectory () {

    // delegate
    final File userHomeDirectory = new File (System.getProperty ("user.home"));
    return new File (userHomeDirectory, "icmp4j");
  }
}