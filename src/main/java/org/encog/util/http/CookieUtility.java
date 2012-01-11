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

import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class allows URLConnection objects to process cookies. The loadCookies
 * method grabs the Set-Cookie headers and loads the cookies into the map. The
 * saveCookies method writes out the cookie map to the a URLConnection object as
 * Cookie headers.
 */
public class CookieUtility {
	/**
	 * Map that holds all of the cookie values.
	 */
	private final Map<String, String> map = new HashMap<String, String>();

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
			if ((str != null) && str.equalsIgnoreCase("Set-Cookie")) {
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
