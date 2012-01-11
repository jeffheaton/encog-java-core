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
package org.encog.util.http;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * URLUtility: A set of useful utilities for processing URL's.
 */
public final class URLUtility {

	/**
	 * Beyond this number are special chars.
	 */
	public static final int SPECIAL_CHAR_LIMIT = 255;

	/**
	 * The name of the usual default document.
	 */
	public static final String INDEX_FILE = "index.html";

	/**
	 * Construct a URL from its basic parts.
	 * 
	 * @param base
	 *            The base URL.
	 * @param url
	 *            The URL that was found on the base URL's page.
	 * @param stripRef
	 *            True if the URL's reference should be stripped.
	 * @return The new URL, built upon the base URL.
	 * @throws IOException
	 *             Thrown if any IO error occurs.
	 */
	public static URL constructURL(final URL base, final String url,
			final boolean stripRef) throws IOException {
		URL result = new URL(base, url);
		String file = result.getFile();
		final String protocol = result.getProtocol();
		final String host = result.getHost();
		final int port = result.getPort();
		final String ref = result.getRef();
		final StringBuilder sb = new StringBuilder(file);
		int index = sb.indexOf(" ");
		while (index != -1) {
			if (index != -1) {
				sb.replace(index, index + 1, "%20");
			}
			index = sb.indexOf(" ");
		}

		file = sb.toString();
		if ((ref != null) && !stripRef) {
			result = new URL(protocol, host, port, file + "#" + ref);
		} else {
			result = new URL(protocol, host, port, file);
		}
		return result;
	}

	/**
	 * Returns true if the URL contains any invalid characters.
	 * 
	 * @param url
	 *            The URL to be checked.
	 * @return True if the URL contains invalid characters.
	 */
	public static boolean containsInvalidURLCharacters(final String url) {
		for (int i = 0; i < url.length(); i++) {
			final char ch = url.charAt(i);
			if (ch > URLUtility.SPECIAL_CHAR_LIMIT) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Convert a filename for local storage. Also create the directory tree.
	 * 
	 * @param base
	 *            The local path that forms the base of the downloaded web tree.
	 * @param url
	 *            The URL path.
	 * @param mkdir
	 *            Should the directory structure be created.
	 * @return The resulting local path to store to.
	 */
	public static String convertFilename(final String base, final URL url,
			final boolean mkdir) {
		final StringBuilder result = new StringBuilder(base);
		int index1;
		int index2;

		// append the host name
		StringBuilder path = new StringBuilder(url.getHost().replace('.', '_'));
		if (url.getFile().length() == 0) {
			path.append('/');
		} else {
			path.append(url.getFile());
		}

		// add ending slash if needed
		if (result.charAt(result.length() - 1) != File.separatorChar) {
			result.append(File.separator);
		}

		// see if an ending / is missing from a directory only

		int lastSlash = path.lastIndexOf("" + File.separatorChar);
		final int lastDot = path.lastIndexOf(".");

		if (path.charAt(path.length() - 1) != '/') {
			if (lastSlash > lastDot) {
				path.append(File.pathSeparatorChar);
				path.append(URLUtility.INDEX_FILE);
			}
		}

		// determine actual filename
		lastSlash = path.lastIndexOf("/");

		String filename = "";
		if (lastSlash != -1) {
			filename = path.substring(1 + lastSlash);
			path = path.replace(1 + lastSlash, path.length(), "");

			if (filename.equals("")) {
				filename = URLUtility.INDEX_FILE;
			}
		}
		// create the directory structure, if needed

		index1 = 0;
		do {
			index2 = path.indexOf("/", index1);

			if (index2 != -1) {
				final String dirpart = path.substring(index1, index2);
				result.append(dirpart);
				result.append(File.separator);

				if (mkdir) {
					final File f = new File(result.toString());
					f.mkdir();
				}

				index1 = index2 + 1;

			}
		} while (index2 != -1);

		// attach name
		result.append(filename.replace('?', '_'));

		return result.toString();
	}

	/**
	 * Private constructor.
	 */
	private URLUtility() {
	}
}
