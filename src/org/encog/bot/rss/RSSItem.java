/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is the class that holds individual RSS items, or stories, for the RSS
 * class.
 */
public class RSSItem {

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The title of this item.
	 */
	private String title;

	/**
	 * The hyperlink to this item.
	 */
	private String link;

	/**
	 * The description of this item.
	 */
	private String description;

	/**
	 * The date this item was published.
	 */
	private Date date;

	/**
	 * Get the publication date.
	 *
	 * @return The publication date.
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * Get the description.
	 *
	 * @return The description.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Get the hyperlink.
	 *
	 * @return The hyperlink.
	 */
	public String getLink() {
		return this.link;
	}

	/**
	 * Get the item title.
	 *
	 * @return The item title.
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Load an item from the specified node.
	 *
	 * @param node
	 *            The Node to load the item from.
	 */
	public void load(final Node node) {
		final NodeList nl = node.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			final Node n = nl.item(i);
			final String name = n.getNodeName();

			if (name.equalsIgnoreCase("title")) {
				this.title = RSS.getXMLText(n);
			} else if (name.equalsIgnoreCase("link")) {
				this.link = RSS.getXMLText(n);
			} else if (name.equalsIgnoreCase("description")) {
				this.description = RSS.getXMLText(n);
			} else if (name.equalsIgnoreCase("pubDate")) {
				final String str = RSS.getXMLText(n);
				if (str != null) {
					this.date = RSS.parseDate(str);
				}
			}

		}
	}

	/**
	 * Set the publication date.
	 *
	 * @param date
	 *            The new publication date.
	 */
	public void setDate(final Date date) {
		this.date = date;
	}

	/**
	 * Get the description.
	 *
	 * @param description
	 *            The new description.
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Set the hyperlink.
	 *
	 * @param link
	 *            The new hyperlink.
	 */
	public void setLink(final String link) {
		this.link = link;
	}

	/**
	 * Set the item title.
	 *
	 * @param title
	 *            The new item title.
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append('[');
		builder.append("title=\"");
		builder.append(this.title);
		builder.append("\",link=\"");
		builder.append(this.link);
		builder.append("\",date=\"");
		builder.append(this.date);
		builder.append("\"]");
		return builder.toString();
	}
}
