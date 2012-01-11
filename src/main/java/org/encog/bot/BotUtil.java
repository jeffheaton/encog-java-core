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
package org.encog.bot;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.encog.parse.tags.read.ReadHTML;
import org.encog.util.logging.EncogLogging;

/**
 * Utility class for bots.
 * 
 * @author jheaton
 * 
 */
public final class BotUtil {

	/**
	 * How much data to read at once.
	 */
	public static final int BUFFER_SIZE = 32768;

	/**
	 * Load the specified URL to a file.
	 * 
	 * @param url
	 *            The URL.
	 * @param file
	 *            The file.
	 */
	public static void downloadPage(final URL url, final File file) {
		FileOutputStream fos = null;
		try {
			final byte[] buffer = new byte[BotUtil.BUFFER_SIZE];

			int length;

			fos = new FileOutputStream(file);
			final InputStream is = url.openStream();

			do {
				length = is.read(buffer);

				if (length >= 0) {
					fos.write(buffer, 0, length);
				}
			} while (length >= 0);

			fos.close();
		} catch (final IOException e) {
			EncogLogging.log(e);
			throw new BotError(e);
		} finally {
			if( fos!=null ) {
				try {
					fos.close();
				} catch (IOException e) {
					EncogLogging.log(e);
				}	
			}
		}
	}

	/**
	 * This method is very useful for grabbing information from a HTML page.
	 * 
	 * @param str
	 *            The string to search.
	 * @param token1
	 *            The text, or tag, that comes before the desired text
	 * @param token2
	 *            The text, or tag, that comes after the desired text
	 * @param index
	 *            Which occurrence of token1 to use, 1 for the first
	 * @return The contents of the URL that was downloaded.
	 */
	public static String extract(final String str, final String token1,
			final String token2, final int index) {
		int location1, location2;

		// convert everything to lower case
		final String searchStr = str.toLowerCase();
		final String token1Lower = token1.toLowerCase();
		final String token2Lower = token2.toLowerCase();

		int count = index;

		// now search
		location1 = -1;
		location2 = -1;
		do {
			location1 = searchStr.indexOf(token1Lower, location1 + 1);

			if (location1 == -1) {
				return null;
			}

			count--;
		} while (count > 0);

		// return the result from the original string that has mixed
		// case
		location2 = searchStr.indexOf(token2Lower, location1 + 1);
		if (location2 == -1) {
			return null;
		}

		return str.substring(location1 + token1Lower.length(), location2);
	}

	/**
	 * This method is very useful for grabbing information from a HTML page.
	 * 
	 * @param str
	 *            The string to search.
	 * @param token1
	 *            The text, or tag, that comes before the desired text
	 * @param token2
	 *            The text, or tag, that comes after the desired text
	 * @param index
	 *            Index in the string to start searching from.
	 * @param occurence
	 *            What occurrence.
	 * @return The contents of the URL that was downloaded.
	 */
	public static String extractFromIndex(final String str,
			final String token1, final String token2, final int index,
			final int occurence) {
		int location1, location2;

		// convert everything to lower case
		final String searchStr = str.toLowerCase();
		final String token1Lower = token1.toLowerCase();
		final String token2Lower = token2.toLowerCase();

		int count = occurence;

		// now search
		location1 = index - 1;
		location2 = location1;
		do {
			location1 = searchStr.indexOf(token1Lower, location1 + 1);

			if (location1 == -1) {
				return null;
			}

			count--;
		} while (count > 0);

		// return the result from the original string that has mixed
		// case
		location2 = searchStr.indexOf(token2Lower, location1 + 1);
		if (location2 == -1) {
			return null;
		}

		return str.substring(location1 + token1Lower.length(), location2);
	}

	/**
	 * Find the specified occurrence of one string in another string.
	 * 
	 * @param search
	 *            The string to search.
	 * @param searchFor
	 *            What we are searching for.
	 * @param index
	 *            The occurrence to find.
	 * @return The index of the specified string, or -1 if not found.
	 */
	public static int findOccurance(final String search,
			final String searchFor, final int index) {
		int count = index;
		final String lowerSearch = search.toLowerCase();
		int result = -1;

		do {
			result = lowerSearch.indexOf(searchFor, result + 1);
		} while (count-- > 0);

		return result;
	}

	/**
	 * Load load from the specified input stream.
	 * 
	 * @param is
	 *            The input stream to load from.
	 * @return The data loaded from the specified input stream.
	 */
	public static String loadPage(final InputStream is) {
		try {
			final StringBuilder result = new StringBuilder();
			final byte[] buffer = new byte[BotUtil.BUFFER_SIZE];

			int length;

			do {
				length = is.read(buffer);
				if (length >= 0) {
					result.append(new String(buffer, 0, length));
				}
			} while (length >= 0);

			return result.toString();
		} catch (final IOException e) {
			EncogLogging.log(e);
			throw new BotError(e);
		}
	}

	/**
	 * Load the specified web page into a string.
	 * 
	 * @param url
	 *            The url to load.
	 * @return The web page as a string.
	 */
	public static String loadPage(final URL url) {
		try {
			final StringBuilder result = new StringBuilder();
			final byte[] buffer = new byte[BotUtil.BUFFER_SIZE];

			int length;

			final InputStream is = url.openStream();

			do {
				length = is.read(buffer);
				if (length >= 0) {
					result.append(new String(buffer, 0, length));
				}
			} while (length >= 0);

			return result.toString();
		} catch (final IOException e) {
			EncogLogging.log(e);
			throw new BotError(e);
		}
	}

	/**
	 * Strip any HTML or XML tags from the specified string.
	 * 
	 * @param str
	 *            The string to process.
	 * @return The string without tags.
	 */
	public static String stripTags(final String str) {
		final ByteArrayInputStream is 
		= new ByteArrayInputStream(str.getBytes());
		final StringBuilder result = new StringBuilder();
		final ReadHTML html = new ReadHTML(is);
		int ch;
		while ((ch = html.read()) != -1) {
			if (ch != 0) {
				result.append((char) ch);
			}
		}
		return result.toString();
	}

	/**
	 * Private constructor.
	 */
	private BotUtil() {

	}
}
