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
package org.encog.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.encog.EncogError;
import org.encog.parse.tags.Tag;
import org.encog.parse.tags.read.ReadHTML;
import org.encog.util.http.FormUtility;

/**
 * YahooSearch: Perform a search using Yahoo.
 */
public class YahooSearch {

	/**
	 * How many retries.
	 */
	private static final int MAX_TRIES = 5;

	/**
	 * How long to sleep between retry.
	 */
	private static final long RETRY_SLEEP = 5000;

	/**
	 * Do a search using the Yahoo search engine. Called internally.
	 * 
	 * @param url
	 *            The Yahoo URL.
	 * @return A collection of URL's.
	 * @throws IOException
	 *             An error occured communicating with Yahoo.
	 */
	private Collection<URL> doSearch(final URL url) throws IOException {
		final Collection<URL> result = new ArrayList<URL>();
		// submit the search

		final InputStream is = url.openStream();
		final ReadHTML parse = new ReadHTML(is);
		final StringBuilder buffer = new StringBuilder();
		boolean capture = false;

		// parse the results
		int ch;
		while ((ch = parse.read()) != -1) {
			if (ch == 0) {
				final Tag tag = parse.getTag();
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
	 * @param searchFor
	 *            What to search for.
	 * @return The URL's found for the specific search.
	 * @throws IOException
	 *             Error connecting to Yahoo.
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
				if (tries == YahooSearch.MAX_TRIES) {
					throw e;
				}
				try {
					Thread.sleep(YahooSearch.RETRY_SLEEP);
				} catch (final InterruptedException e1) {
					throw new EncogError("Interrupted");
				}
			}
			tries++;
		}

		return result;

	}

}
