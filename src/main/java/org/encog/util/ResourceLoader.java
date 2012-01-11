/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.encog.EncogError;

/**
 * Used to load resources from the JAR file.
 */
public final class ResourceLoader {

	/**
	 * Create an input stream to read from the resource.
	 * @param resource The resource to load.
	 * @return An input stream.
	 */
	public static InputStream createInputStream(
			final String resource) {

		final ClassLoader loader = ResourceLoader.class.getClassLoader();
		final InputStream is = loader.getResourceAsStream(resource);

		if (is == null) {
			final String str = "Can't read resource: " + resource;
			throw new EncogError(str);
		}
		return is;
	}

	/**
	 * Load the resource as a string.
	 * @param resource The resource to load.
	 * @return The resource as a string.
	 */
	public static String loadString(final String resource) {
		InputStream is = null;
		try {
			is = ResourceLoader.createInputStream(resource);
			final StringBuilder result = new StringBuilder();
			final BufferedReader br = new BufferedReader(new InputStreamReader(
					is));
			String line;
			while ((line = br.readLine()) != null) {
				result.append(line);
				result.append("\r\n");
			}
			return result.toString();
		} catch (final IOException e) {
			throw new EncogError(e);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (final IOException e) {
				throw new EncogError(e);
			}
		}
	}

	/**
	 * Private constructor.
	 */
	private ResourceLoader() {

	}
}
