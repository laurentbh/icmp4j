package org.icmp4j.util;

import java.util.Set;
import java.util.HashSet;
import java.io.File;

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
 * User: Laurent Buhler
 * Date: Feb 1, 2016
 * Time: 5:02:49 PM
 */
public class JniUtil {
	// my attributes
	private static final Logger logger = Logger.getLogger (JniUtil.class);
	private static final Set<String> librarySet = new HashSet<String>();

	/**
	 * Loads the given library
	 * 
	 * This method loads the library natively via System.loadLibrary ().
	 * If that fails, this method looks for the library as a resource (meaning inside jars, wars, ...),
	 * extracts it, and then loads it.
	 * If that fails as well, this throws a UnsatisfiedLinkError 
	 * 
	 * @param libraryName : name of the library to load without prefix or extension
	 */
	public static synchronized void loadLibraryBestEffort (final String libraryName) 
    throws UnsatisfiedLinkError {
		
    // already loaded?
		if ( librarySet.contains(libraryName))
			return;

		// the library has not yet been loaded: load it
		boolean isLoaded = false;

		// try to load from the java.library.path
		logger.info ("loadLibrary trying to load " + libraryName + " from java.library.path");
		try {
			System.loadLibrary (libraryName);
			isLoaded = true;
		} catch (UnsatisfiedLinkError e) {
			logger.warn ("loadLibrary can't load " + libraryName + " from java.library.path " +
					"exception " + e.getClass ().getName () + ", " +
					"errorMessage: " + ExceptionUtil.getMessage (e));
		}

		if ( isLoaded == false ) {
			try {
				final String libraryNameInJar = ResourceUtil.buildLibraryName(libraryName);
				final File file = SystemUtil.extractLibraryByResource(libraryNameInJar);
				final String path = file.getAbsolutePath();
				logger.info("extracted lib in : " + path);
				try {
					System.load(path);
					isLoaded = true;
				} catch (UnsatisfiedLinkError e) {
					logger.warn ("loadLibrary can't load " + libraryName + " from " + path +" " +
						"exception " + e.getClass ().getName () + ", " +
						"errorMessage: " + ExceptionUtil.getMessage (e));
				}
			} catch (AssertRuntimeException e) {
				logger.warn(e.getMessage());

			}
		}

		if ( isLoaded == false ) {
      final UnsatisfiedLinkError e = new UnsatisfiedLinkError ("Failed to load library " + libraryName);
      throw e;
    }
		librarySet.add (libraryName);
	}
}