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
package org.encog.bot.browse;

import java.util.ArrayList;
import java.util.List;

import org.encog.bot.browse.range.DocumentRange;
import org.encog.bot.browse.range.Link;
import org.encog.bot.dataunit.DataUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds a web page that was loaded by the Browse class. Web pages are made up
 * of DataUnits and contents, which are ranges of data units. The data units are
 * basically tags and blocks of text. The contents collection uses DocumentRange
 * objects to assign meatning to the lower level DataObjects.
 * 
 * @author jheaton
 * 
 */
public class WebPage {

	/**
	 * The data units that make up this page.
	 */
	private final List<DataUnit> data = new ArrayList<DataUnit>();

	/**
	 * The contents of this page, builds upon the list of DataUnits.
	 */
	private final List<DocumentRange> contents = new ArrayList<DocumentRange>();

	/**
	 * The title of this HTML page.
	 */
	private DocumentRange title;

	/**
	 * The logger.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Add to the content collection.
	 * 
	 * @param span
	 *            The range to add to the collection.
	 */
	public void addContent(final DocumentRange span) {
		span.setSource(this);
		this.contents.add(span);
	}

	/**
	 * Add a data unit to the collection.
	 * 
	 * @param unit
	 *            The data unit to load.
	 */
	public void addDataUnit(final DataUnit unit) {
		this.data.add(unit);
	}

	/**
	 * Find the specified DocumentRange subclass in the contents list.
	 * 
	 * @param c
	 *            The class type to search for.
	 * @param index
	 *            The index to search from.
	 * @return The document range that was found.
	 */
	public DocumentRange find(final Class<?> c, final int index) {
		int i = index;
		for (final DocumentRange span : getContents()) {
			if (span.getClass().getName().equals(c.getName())) {
				if (i <= 0) {
					return span;
				}
				i--;
			}
		}
		return null;

	}

	/**
	 * Find the link that contains the specified string.
	 * 
	 * @param str
	 *            The string to search for.
	 * @return The link that contains the specified string.
	 */
	public Link findLink(final String str) {
		for (final DocumentRange span : getContents()) {
			if (span instanceof Link) {
				final Link link = (Link) span;
				if (link.getTextOnly().equals(str)) {
					return link;
				}
			}
		}
		return null;
	}

	/**
	 * @return The contents in a list collection.
	 */

	public List<DocumentRange> getContents() {
		return this.contents;
	}

	/**
	 * @return The data units in a list collection.
	 */
	public List<DataUnit> getData() {
		return this.data;
	}

	/**
	 * Get the number of data items in this collection.
	 * 
	 * @return The size of the data unit.
	 */
	public int getDataSize() {
		return this.data.size();
	}

	/**
	 * Get the DataUnit unit at the specified index.
	 * 
	 * @param i
	 *            The index to use.
	 * @return The DataUnit found at the specified index.
	 */
	public DataUnit getDataUnit(final int i) {
		return this.data.get(i);
	}

	/**
	 * Get the title for this document.
	 * 
	 * @return The DocumentRange that specifies the title of this document.
	 */
	public DocumentRange getTitle() {
		return this.title;
	}

	/**
	 * Set the title of this document.
	 * 
	 * @param title
	 *            The DocumentRange that specifies the title.
	 */
	public void setTitle(final DocumentRange title) {
		this.title = title;
		this.title.setSource(this);
	}

	/**
	 * @return The object as a string.
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		for (final DocumentRange span : getContents()) {
			result.append(span.toString());
			result.append("\n");
		}
		return result.toString();
	}
}
