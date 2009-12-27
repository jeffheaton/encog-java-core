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
	 * Convert the object to a String.
	 * 
	 * @return The object as a String.
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
