/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.engine.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.encog.engine.EncogEngineError;


public class ResourceLoader  {



	/**
	 * Create an input stream to read from the resource.
	 * 
	 * @return An input stream.
	 */
	public static InputStream createInputStream(String resource) {

				
		final ClassLoader loader = ResourceLoader.class.getClassLoader();
		final InputStream is = loader.getResourceAsStream(resource);

		if (is == null) {
			final String str = "Can't read resource: " + resource;
			throw new EncogEngineError(str);
		}
		return is;
	}

	/**
	 * Load the resource as a string.
	 * 
	 * @return The resource as a string.
	 */
	public static String loadString(String resource) {
		InputStream is = null;
		try {
			is = createInputStream(resource);
			StringBuilder result = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = br.readLine()) != null) {
				result.append(line);
				result.append("\r\n");
			}
			return result.toString();
		} catch (IOException e) {
			throw new EncogEngineError(e);
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				throw new EncogEngineError(e);
			}
		}
	}
}
