/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */

package org.encog.bot;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/*
 * Encog Artificial Intelligence Framework v2.x Java Version
 * http://www.heatonresearch.com/encog/ http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors. See
 * the copyright.txt in the distribution for a full listing of individual
 * contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

import org.encog.parse.tags.read.ReadHTML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public static final int BUFFER_SIZE = 8192;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(BotUtil.class);

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
			if (BotUtil.LOGGER.isErrorEnabled()) {
				BotUtil.LOGGER.error("Exception", e);
			}
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
			if (BotUtil.LOGGER.isErrorEnabled()) {
				BotUtil.LOGGER.error("Exception", e);
			}
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
		final ByteArrayInputStream is = 
			new ByteArrayInputStream(str.getBytes());
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
