/*
  * Encog Neural Network and Bot Library for Java
  * http://www.heatonresearch.com/encog/
  * http://code.google.com/p/encog-java/
  * Copyright 2008, Heaton Research Inc., and individual contributors as indicated
  * by the @authors tag. See the copyright.txt in the distribution for a
  * full listing of individual contributors.
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
package org.encog.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.encog.bot.html.FormUtility;
import org.encog.bot.html.HTMLTag;
import org.encog.bot.html.ParseHTML;


/**
 * YahooSearch: Perform a search using Yahoo.
 *  
 * @author Jeff Heaton
 * @version 2.1
 */
public class YahooSearch {

	private Collection<URL> doSearch(final URL url) throws IOException {
		final Collection<URL> result = new ArrayList<URL>();
		// submit the search

		final InputStream is = url.openStream();
		final ParseHTML parse = new ParseHTML(is);
		final StringBuilder buffer = new StringBuilder();
		boolean capture = false;

		// parse the results
		int ch;
		while ((ch = parse.read()) != -1) {
			if (ch == 0) {
				final HTMLTag tag = parse.getTag();
				if (tag.getName().equalsIgnoreCase("url")) {
					buffer.setLength(0);
					capture = true;
				} else if (tag.getName().equalsIgnoreCase("/url")) {
					result.add(new URL(buffer.toString()));
					buffer.setLength(0);
					capture = false;
				}
			} else {
				if (capture) {
					buffer.append((char) ch);
				}
			}
		}
		return result;
	}

	/**
	 * Called to extract a list from the specified URL.
	 * 
	 * @param url
	 *            The URL to extract the list from.
	 * @param listType
	 *            What type of list, specify its beginning tag (i.e.
	 *            <UL>)
	 * @param optionList
	 *            Which list to search, zero for first.
	 * @throws IOException
	 *             Thrown if an IO exception occurs.
	 */
	public Collection<URL> search(final String searchFor) throws IOException {
		Collection<URL> result = null;

		// build the URL
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final FormUtility form = new FormUtility(bos, null);
		form.add("appid", "YahooDemo");
		form.add("results", "100");
		form.add("query", searchFor);
		form.complete();

		final URL url = new URL(
				"http://search.yahooapis.com/WebSearchService/V1/webSearch?"
						+ bos.toString());
		bos.close();

		int tries = 0;
		boolean done = false;
		while (!done) {
			try {
				result = doSearch(url);
				done = true;
			} catch (final IOException e) {
				if (tries == 5) {
					throw (e);
				}
				try {
					Thread.sleep(5000);
				} catch (final InterruptedException e1) {
				}
			}
			tries++;
		}

		return result;

	}

}