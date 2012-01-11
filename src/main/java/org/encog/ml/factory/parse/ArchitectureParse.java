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
package org.encog.ml.factory.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.EncogError;
import org.encog.util.SimpleParser;
import org.encog.util.logging.EncogLogging;

/**
 * This class is used to parse a Encog architecture string.
 */
public final class ArchitectureParse {

	/**
	 * Private constructor.
	 */
	private ArchitectureParse() {		
	}
	
	
	/**
	 * parse a layer.
	 * @param line The line to parse.
	 * @param defaultValue The default value.
	 * @return The parsed ArchitectureLayer.
	 */
	public static ArchitectureLayer parseLayer(final String line,
			final int defaultValue) {
		final ArchitectureLayer layer = new ArchitectureLayer();

		String check = line.trim().toUpperCase();

		// first check for bias
		if (check.endsWith(":B")) {
			check = check.substring(0, check.length() - 2);
			layer.setBias(true);
		}

		// see if simple number
		try {
			layer.setCount(Integer.parseInt(check));
			if (layer.getCount() < 0) {
				throw new EncogError("Count cannot be less than zero.");
			}
		} catch (final NumberFormatException f) {
			// don't really care!  Just checking to see if its a number.
		}

		// see if it is a default
		if ("?".equals(check)) {
			if (defaultValue < 0) {
				throw new EncogError("Default (?) in an invalid location.");
			} else {
				layer.setCount(defaultValue);
				layer.setUsedDefault(true);
				return layer;
			}
		}

		// single item, no function
		final int startIndex = check.indexOf('(');
		final int endIndex = check.lastIndexOf(')');
		if (startIndex == -1) {
			layer.setName(check);
			return layer;
		}

		// function
		if (endIndex == -1) {
			throw new EncogError("Illegal parentheses.");
		}

		layer.setName(check.substring(0, startIndex).trim());

		final String paramStr = check.substring(startIndex + 1, endIndex);
		final Map<String, String> params = ArchitectureParse
				.parseParams(paramStr);
		layer.getParams().putAll(params);
		return layer;
	}

	/**
	 * Parse all layers from a line of text.
	 * @param line The line of text.
	 * @return A list of the parsed layers.
	 */
	public static List<String> parseLayers(final String line) {
		final List<String> result = new ArrayList<String>();

		int base = 0;
		boolean done = false;

		do {
			String part;
			final int index = line.indexOf("->", base);
			if (index != -1) {
				part = line.substring(base, index).trim();
				base = index + 2;
			} else {
				part = line.substring(base).trim();
				done = true;
			}

			final boolean bias = part.endsWith("b");
			if (bias) {
				part = part.substring(0, part.length() - 1);
			}

			result.add(part);

		} while (!done);

		return result;
	}

	/**
	 * Parse a name.
	 * @param parser The parser to use.
	 * @return The name.
	 */
	private static String parseName(final SimpleParser parser) {
		final StringBuilder result = new StringBuilder();
		parser.eatWhiteSpace();
		while (parser.isIdentifier()) {
			result.append(parser.readChar());
		}
		return result.toString();
	}

	/**
	 * Parse parameters.
	 * @param line The line to parse.
	 * @return The parsed values.
	 */
	public static Map<String, String> parseParams(final String line) {
		final Map<String, String> result = new HashMap<String, String>();

		final SimpleParser parser = new SimpleParser(line);

		while (!parser.eol()) {
			final String name = ArchitectureParse.parseName(parser)
					.toUpperCase();

			parser.eatWhiteSpace();
			if (!parser.lookAhead("=", false)) {
				throw new EncogError("Missing equals(=) operator.");
			} else {
				parser.advance();
			}

			final String value = ArchitectureParse.parseValue(parser);

			result.put(name.toUpperCase(), value);

			if (!parser.parseThroughComma()) {
				break;
			}
		}

		return result;
	}

	/**
	 * Parse a value.
	 * @param parser The parser to use.
	 * @return The newly parsed value.
	 */
	private static String parseValue(final SimpleParser parser) {
		boolean quoted = false;
		final StringBuilder str = new StringBuilder();

		parser.eatWhiteSpace();

		if (parser.peek() == '\"') {
			quoted = true;
			parser.advance();
		}

		while (!parser.eol()) {
			if (parser.peek() == '\"') {
				if (quoted) {
					parser.advance();
					if (parser.peek() == '\"') {
						str.append(parser.readChar());
					} else {
						break;
					}
				} else {
					str.append(parser.readChar());
				}
			} else if (!quoted
					&& (parser.isWhiteSpace() || (parser.peek() == ','))) {
				break;
			} else {
				str.append(parser.readChar());
			}
		}
		return str.toString();

	}
}
