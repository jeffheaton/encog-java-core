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
package org.encog.util.http;

import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class allows URLConnection objects to process cookies.
 * The loadCookies method grabs the Set-Cookie headers and loads the cookies
 * into the map. The saveCookies method writes out the cookie map to the a
 * URLConnection object as Cookie headers.
 */
public class CookieUtility {
	/**
	 * Map that holds all of the cookie values.
	 */
	private final Map<String, String> map = new HashMap<String, String>();
	
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Allows access to the name/value pair list of cookies.
	 * 
	 * @return the map
	 */
	public Map<String, String> getMap() {
		return this.map;
	}

	/**
	 * Load any cookies from the specified URLConnection object. Cookies will be
	 * located by their Set-Cookie headers. Any cookies that are found can be
	 * moved to a new URLConnection class by calling saveCookies.
	 * 
	 * @param http
	 *            The URLConnection object to load the cookies from.
	 */
	public void loadCookies(final URLConnection http) {
		String str;
		int n = 1;

		do {
			str = http.getHeaderFieldKey(n);
			if (str != null && str.equalsIgnoreCase("Set-Cookie")) {
				str = http.getHeaderField(n);
				final StringTokenizer tok = new StringTokenizer(str, "=");
				final String name = tok.nextToken();
				final String value = tok.nextToken();
				this.map.put(name, value);
			}
			n++;
		} while (str != null);
	}

	/**
	 * Once you have loaded cookies with loadCookies, you can call saveCookies
	 * to copy these cookies to a new HTTP request. This allows you to easily
	 * support cookies.
	 * 
	 * @param http
	 *            The URLConnection object to add cookies to.
	 */
	public void saveCookies(final URLConnection http) {
		final StringBuilder str = new StringBuilder();

		final Set<String> set = this.map.keySet();
		for (final String key : set) {
			final String value = this.map.get(key);
			if (str.length() > 0) {
				str.append("; ");
			}

			str.append(key + "=" + value);
		}

		http.setRequestProperty("Cookie", str.toString());
	}
}
