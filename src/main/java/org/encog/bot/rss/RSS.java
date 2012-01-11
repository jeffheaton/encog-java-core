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
package org.encog.bot.rss;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.encog.bot.BotError;
import org.encog.util.logging.EncogLogging;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This is the class that actually parses the RSS and builds a collection of
 * RSSItems. To make use of this class call the load method with a URL that
 * points to RSS.
 */
public class RSS {

	/**
	 * Simple utility method that obtains the text of an XML node.
	 * 
	 * @param n
	 *            The XML node.
	 * @return The text of the specified XML node.
	 */
	public static String getXMLText(final Node n) {
		final NodeList list = n.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			final Node n2 = list.item(i);
			if (n2.getNodeType() == Node.TEXT_NODE) {
				return n2.getNodeValue();
			}
		}
		return null;
	}

	/**
	 * Simple utility function that converts a RSS formatted date into a Java
	 * date.
	 * 
	 * @param datestr
	 *            The RSS formatted date.
	 * @return A Java java.util.date
	 */
	public static Date parseDate(final String datestr) {
		try {
			final DateFormat formatter = new SimpleDateFormat(
					"E, dd MMM yyyy HH:mm:ss Z");
			final Date date = formatter.parse(datestr);
			return date;
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * All of the attributes for this RSS document.
	 */
	private final Map<String, String> attributes 
		= new HashMap<String, String>();

	/**
	 * All RSS items, or stories, found.
	 */
	private final List<RSSItem> items = new ArrayList<RSSItem>();

	/**
	 * Get the list of attributes.
	 * 
	 * @return the attributes
	 */
	public final Map<String, String> getAttributes() {
		return this.attributes;
	}

	/**
	 * @return the items
	 */
	public final List<RSSItem> getItems() {
		return this.items;
	}

	/**
	 * Load all RSS data from the specified URL.
	 * 
	 * @param url The URL to load.
	 */
	public final void load(final URL url) {
		load(url, -1);
	}

	/**
	 * Load all RSS data from the specified URL.
	 * 
	 * @param url
	 *            URL that contains XML data.
	 * @param timeout The timeout value.
	 */
	public final void load(final URL url, final int timeout) {
		try {
			EncogLogging.log(EncogLogging.LEVEL_DEBUG, "Loading RSS from:"
					+ url);

			final URLConnection http = url.openConnection();
			if (timeout > 0) {
				http.setConnectTimeout(timeout);
			}
			http.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; " 
					+ "Win64; x64; Trident/4.0)");
			final InputStream is = http.getInputStream();

			final DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			final Document d = factory.newDocumentBuilder().parse(is);

			final Element e = d.getDocumentElement();
			final NodeList nl = e.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				final Node node = nl.item(i);
				final String nodename = node.getNodeName();

				// RSS 2.0
				if (nodename.equalsIgnoreCase("channel")) {
					loadChannel(node);
					// RSS 1.0
				} else if (nodename.equalsIgnoreCase("item")) {
					loadItem(node);
				}
			}
		} catch (final IOException e) {
			EncogLogging.log(e);
			throw new BotError(e);
		} catch (final SAXException e) {
			EncogLogging.log(e);
			throw new BotError(e);
		} catch (final ParserConfigurationException e) {
			EncogLogging.log(e);
			throw new BotError(e);
		}
	}

	/**
	 * Load the channle node.
	 * 
	 * @param channel
	 *            A node that contains a channel.
	 */
	private void loadChannel(final Node channel) {
		final NodeList nl = channel.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			final Node node = nl.item(i);
			final String nodename = node.getNodeName();
			if (nodename.equalsIgnoreCase("item")) {
				loadItem(node);
			} else {
				if (node.getNodeType() != Node.TEXT_NODE) {
					this.attributes.put(nodename, RSS.getXMLText(node));
				}
			}
		}
	}

	/**
	 * Load the specified RSS item, or story.
	 * 
	 * @param item
	 *            A XML node that contains a RSS item.
	 */
	private void loadItem(final Node item) {
		final RSSItem rssItem = new RSSItem();
		rssItem.load(item);
		this.items.add(rssItem);
		EncogLogging
				.log(EncogLogging.LEVEL_DEBUG, "Loaded RSS item:" + rssItem);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		final StringBuilder str = new StringBuilder();
		final Set<String> set = this.attributes.keySet();
		for (final String item : set) {
			str.append(item);
			str.append('=');
			str.append(this.attributes.get(item));
			str.append('\n');
		}
		str.append("Items:\n");
		for (final RSSItem item : this.items) {
			str.append(item.toString());
			str.append('\n');
		}
		return str.toString();
	}

}
