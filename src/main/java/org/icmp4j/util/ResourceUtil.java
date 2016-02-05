package org.icmp4j.util;

import java.io.File;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;

import org.icmp4j.constants.OsFamilyCode;
import org.icmp4j.exception.AssertRuntimeException;
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
 * Date: Nov 22, 2009
 * Time: 11:01:45 AM
 */
public class ResourceUtil {

  // my attributes
  private static final Logger logger = Logger.getLogger (ResourceUtil.class);

  /**
   * Returns a File to the given resource
   * Returns null if not found
   * @param resourceName
   * @throws AssertRuntimeException if the resource is found, but its file is not
   * @return InputStream
   */
  public static File findResourceAsFile (
    final String resourceName)
      throws AssertRuntimeException {

    // delegate
    final URL urlObject = findResourceAsURL (resourceName);
    if (urlObject == null) {
      return null;
    }

    // handle exceptions
    try {

      // convert to file
      // note that because of the following java-bug, we cannot just use url.getFile ()
      // because it returns encoded paths that can only be decoded by the following URI workaround: 
      // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4466485
      // 
      // WARNING: on Sun 3/30/3014 at ~3 PM, with FF 19 and Java 1.7, suddently fileUri.getPath () started
      // returning null! so i had to add a check for it...
      final String url = urlObject.toString ();
      logger.debug ("url: " + url);

      final URI fileUri = new URI (url);
      logger.debug ("fileUri: " + fileUri);

      final String decodedFilePath = fileUri.getPath ();
      logger.debug ("decodedFilePath: " + decodedFilePath);
      if (decodedFilePath == null) {
        logger.warn ("findResourceAsFile (). resourceName: " + resourceName + ", decodedFilePath is null: returning null");
        return null;
      }

      // lookup
      final File file = new File (decodedFilePath);
      if (!file.exists ()) {
        throw new AssertRuntimeException (
          "resourceName " + resourceName + " " +
          "found in url " + url + " but " +
          "file does not exist: " + file.getAbsolutePath ());
      }

      // done
      return file;
    }
    catch (final URISyntaxException e) {

      // log
      logger.error ("resourceName: " + resourceName);
      logger.error (e);

      // done
      return null;
    }
  }

  /**
   * @param resourceName
   * @return InputStream
   */
  private static URL findResourceAsURL (final String resourceName) {

    // lookup as is
    {
      final URL url = ResourceUtil.class.getResource (resourceName);
      if (url != null) {
        return url;
      }
    }

    // lookup as an absolute path
    final String fullPath =
      (resourceName.startsWith("/") ? "" : "/") +
      resourceName;
    return ResourceUtil.class.getResource (fullPath);
  }
  /**
	 * return the library name as needed for the current architecture.
	 * ie lib[name].so for linux, lib[name].dylib for osx ..
	 * @param libraryName
	 * @return
	 */
	public static String buildLibraryName (final String libraryName) {
		final StringBuilder sb;

		int os = PlatformUtil.getOsFamilyCode();
		switch (os) {
		case OsFamilyCode.WINDOWS:
			sb = new StringBuilder().append(libraryName).append(".dll");
			return sb.toString();
		case OsFamilyCode.MAC:
			sb = new StringBuilder().append("lib").append(libraryName)
			.append(".dylib");
			return sb.toString();
		case OsFamilyCode.LINUX:
			// assuming that we run a JVM matching the architecture
			String arch = System.getProperty("os.arch");
			if (arch.contains("64")) 
				arch = "64bit";
			else 
				arch = "32bit";
			sb = new StringBuilder().append("lib").append(libraryName)
					.append("_").append(arch).append(".so");
			return sb.toString();
		}
		throw new UnsupportedOperationException("architecture not handle");
	}
}