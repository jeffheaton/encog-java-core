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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import org.encog.bot.BotError;

/**
 * This class is used to construct responses to HTML forms. The class supports
 * both standard HTML forms, as well as multipart forms.
 */
public class FormUtility {
	/**
	 * The charset to use for URL encoding. Per URL coding spec, this value
	 * should always be UTF-8.
	 */
	public static final String ENCODE = "UTF-8";

	/**
	 * The size of the read buffer.
	 */
	public static final int BUFFER_SIZE = 8192;

	/**
	 * The length of random string to create for multipart.
	 */
	public static final int RANDOM_LENGTH = 36;

	/**
	 * A Java random number generator.
	 */
	private static Random random = new Random();

	/**
	 * Encode the specified string. This encodes all special characters.
	 * 
	 * @param str
	 *            The string to encode.
	 * @return The encoded string.
	 */
	private static String encode(final String str) {
		try {
			return URLEncoder.encode(str, FormUtility.ENCODE);
		} catch (final UnsupportedEncodingException e) {
			return str;
		}
	}

	/**
	 * Generate a boundary for a multipart form.
	 * 
	 * @return The boundary.
	 */
	public static String getBoundary() {
		return "---------------------------" + FormUtility.randomString()
				+ FormUtility.randomString() + FormUtility.randomString();
	}

	/**
	 * Parse a URL query string. Return a map of all of the name value pairs.
	 * 
	 * @param form
	 *            The query string to parse.
	 * @return A map of name-value pairs.
	 */
	public static Map<String, String> parse(final String form) {
		final Map<String, String> result = new HashMap<String, String>();
		final StringTokenizer tok = new StringTokenizer(form, "&");
		while (tok.hasMoreTokens()) {
			final String str = tok.nextToken();
			final StringTokenizer tok2 = new StringTokenizer(str, "=");
			if (!tok2.hasMoreTokens()) {
				continue;
			}
			String left = tok2.nextToken();
			if (!tok2.hasMoreTokens()) {
				left = FormUtility.encode(left);
				result.put(left, null);
				continue;
			}
			String right = tok2.nextToken();
			right = FormUtility.encode(right);
			result.put(left, right);
		}
		return result;
	}

	/**
	 * Generate a random string, of a specified length. This is used to generate
	 * the multipart boundary.
	 * 
	 * @return A random string.
	 */
	protected static String randomString() {
		return Long.toString(FormUtility.random.nextLong(),
				FormUtility.RANDOM_LENGTH);
	}

	/**
	 * The boundary used for a multipart post. This field is null if this is not
	 * a multipart form and has a value if this is a multipart form.
	 */
	private final String boundary;

	/**
	 * The stream to output the encoded form to.
	 */
	private final OutputStream os;

	/**
	 * Keep track of if we're on the first form element.
	 */
	private boolean first;

	/**
	 * Prepare to access either a regular, or multipart, form.
	 * 
	 * @param os
	 *            The stream to output to.
	 * @param boundary
	 *            The boundary to be used, or null if this is not a multipart
	 *            form.
	 */
	public FormUtility(final OutputStream os, final String boundary) {
		this.os = os;
		this.boundary = boundary;
		this.first = true;
	}

	/**
	 * Add a file to a multipart form.
	 * 
	 * @param name
	 *            The field name.
	 * @param file
	 *            The file to attach.
	 */
	public void add(final String name, final File file) {
		try {
			if (this.boundary != null) {
				boundary();
				writeName(name);
				write("; filename=\"");
				write(file.getName());
				write("\"");
				newline();
				write("Content-Type: ");
				String type = URLConnection.guessContentTypeFromName(file
						.getName());
				if (type == null) {
					type = "application/octet-stream";
				}
				writeln(type);
				newline();

				final byte[] buf = new byte[FormUtility.BUFFER_SIZE];
				int nread;

				final InputStream in = new FileInputStream(file);
				while ((nread = in.read(buf, 0, buf.length)) >= 0) {
					this.os.write(buf, 0, nread);
				}

				newline();
			}
		} catch (final IOException e) {
			throw new BotError(e);
		}
	}

	/**
	 * Add a regular text field to either a regular or multipart form.
	 * 
	 * @param name
	 *            The name of the field.
	 * @param value
	 *            The value of the field.
	 */
	public void add(final String name, final String value) {
		if (this.boundary != null) {
			boundary();
			writeName(name);
			newline();
			newline();
			writeln(value);
		} else {
			if (!this.first) {
				write("&");
			}
			write(FormUtility.encode(name));
			write("=");
			write(FormUtility.encode(value));
		}
		this.first = false;
	}

	/**
	 * Generate a multipart form boundary.
	 * 
	 */
	private void boundary() {
		write("--");
		write(this.boundary);
	}

	/**
	 * Complete the building of the form.
	 * 
	 */
	public void complete() {
		try {
			if (this.boundary != null) {
				boundary();
				writeln("--");
				this.os.flush();
			}
		} catch (final IOException e) {
			throw (new BotError(e));
		}
	}

	/**
	 * Create a new line by displaying a carriage return and linefeed.
	 * 
	 */
	private void newline() {
		write("\r\n");
	}

	/**
	 * Write the specified string, without a carriage return and line feed.
	 * 
	 * @param str
	 *            The String to write.
	 */
	private void write(final String str) {
		try {
			this.os.write(str.getBytes());
		} catch (final IOException e) {
			throw new BotError(e);
		}
	}

	/**
	 * Write a string, with a carriage return and linefeed.
	 * 
	 * @param str
	 *            The string to write.
	 */
	protected void writeln(final String str) {
		write(str);
		newline();
	}

	/**
	 * Write the name element for a multipart post.
	 * 
	 * @param name
	 *            The name of the field.
	 */
	private void writeName(final String name) {
		newline();
		write("Content-Disposition: form-data; name=\"");
		write(name);
		write("\"");
	}

}
