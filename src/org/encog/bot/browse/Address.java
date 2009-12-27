/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
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
package org.encog.bot.browse;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A URL address. Holds both the URL object, as well as original text.
 * 
 * @author jheaton
 * 
 */
public class Address {
	/**
	 * The original text from the address.
	 */
	private final String original;

	/**
	 * The address as a URL.
	 */
	private URL url;

	/**
	 * The logger.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct the address from a URL.
	 * 
	 * @param u
	 *            The URL to use.
	 */
	public Address(final URL u) {
		this.url = u;
		this.original = u.toString();
	}

	/**
	 * Construct a URL using a perhaps relative URL and a base URL.
	 * 
	 * @param base
	 *            The base URL.
	 * @param original
	 *            A full URL or a URL relative to the base.
	 */
	public Address(final URL base, final String original) {
		this.original = original;
		try {
			this.url = new URL(base, original);
		} catch (final MalformedURLException e) {
			this.logger.debug("Malformed URL", e);
		}
	}

	/**
	 * @return The original text from this URL.
	 */
	public String getOriginal() {
		return this.original;
	}

	/**
	 * @return THe URL.
	 */
	public URL getUrl() {
		return this.url;
	}

	/**
	 * @return The object as a string.
	 */
	@Override
	public String toString() {
		if (this.url != null) {
			return this.url.toString();
		} else {
			return this.original;
		}
	}

}
