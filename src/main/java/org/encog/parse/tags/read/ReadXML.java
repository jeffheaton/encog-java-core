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
package org.encog.parse.tags.read;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.encog.parse.ParseError;
import org.encog.parse.tags.Tag.Type;

/**
 * This class is designed to read XML. It has several helper methods, beyond
 * what the ReadTags class provides to assist in reading XML documents.
 * 
 * @author jheaton
 * 
 */
public class ReadXML extends ReadTags {

	/**
	 * Construct an XML reader.
	 * 
	 * @param is
	 *            The input stream to read from.
	 */
	public ReadXML(final InputStream is) {
		super(is);
	}

	/**
	 * Advance until the specified tag is found.
	 * 
	 * @param name
	 *            The name of the tag we are looking for.
	 * @param beginTag
	 *            True if this is a begin tage, false otherwise.
	 * @return True if the tag was found.
	 */
	public boolean findTag(final String name, final boolean beginTag) {
		while (readToTag()) {
			if (beginTag) {
				if (getTag().getName().equals(name)
						&& (getTag().getType() == Type.BEGIN)) {
					return true;
				}
			} else {
				if (getTag().getName().equals(name)
						&& (getTag().getType() == Type.END)) {
					return true;
				}
			}
		}

		return false;

	}

	/**
	 * Read an integer that is contained between the current position, and the
	 * next tag.
	 * 
	 * @return The integer that was found.
	 */
	public int readIntToTag() {
		try {
			final String str = readTextToTag();
			return Integer.parseInt(str);
		} catch (final NumberFormatException e) {			
			throw new ParseError(e);
		}
	}

	/**
	 * Read all property data until an end tag, which corrisponds to the current
	 * tag, is found. The properties found will be returned in a map.
	 * 
	 * @return The properties found.
	 */
	public Map<String, String> readPropertyBlock() {
		final Map<String, String> result = new HashMap<String, String>();

		final String endingBlock = getTag().getName();

		while (readToTag()) {
			if (getTag().getName().equals(endingBlock)
					&& (getTag().getType() == Type.END)) {
				break;
			}
			final String name = getTag().getName();
			final String value = readTextToTag().trim();
			result.put(name, value);
		}

		return result;
	}

	/**
	 * Read all text between the current position and the next tag.
	 * 
	 * @return The string that was read.
	 */
	public String readTextToTag() {
		final StringBuilder result = new StringBuilder();
		boolean done = false;

		while (!done) {
			final int ch = read();
			if ((ch == -1) || (ch == 0)) {
				done = true;
			} else {
				result.append((char) ch);
			}

		}
		return result.toString();
	}
}
