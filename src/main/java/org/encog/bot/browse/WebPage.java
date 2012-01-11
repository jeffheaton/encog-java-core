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
package org.encog.bot.browse;

import java.util.ArrayList;
import java.util.List;

import org.encog.bot.browse.range.DocumentRange;
import org.encog.bot.browse.range.Link;
import org.encog.bot.dataunit.DataUnit;

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
	 * Add to the content collection.
	 *
	 * @param span
	 *            The range to add to the collection.
	 */
	public final void addContent(final DocumentRange span) {
		span.setSource(this);
		this.contents.add(span);
	}

	/**
	 * Add a data unit to the collection.
	 *
	 * @param unit
	 *            The data unit to load.
	 */
	public final void addDataUnit(final DataUnit unit) {
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
	public final DocumentRange find(final Class< ? > c, final int index) {
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
	public final Link findLink(final String str) {
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

	public final List<DocumentRange> getContents() {
		return this.contents;
	}

	/**
	 * @return The data units in a list collection.
	 */
	public final List<DataUnit> getData() {
		return this.data;
	}

	/**
	 * Get the number of data items in this collection.
	 *
	 * @return The size of the data unit.
	 */
	public final int getDataSize() {
		return this.data.size();
	}

	/**
	 * Get the DataUnit unit at the specified index.
	 *
	 * @param i
	 *            The index to use.
	 * @return The DataUnit found at the specified index.
	 */
	public final DataUnit getDataUnit(final int i) {
		return this.data.get(i);
	}

	/**
	 * Get the title for this document.
	 *
	 * @return The DocumentRange that specifies the title of this document.
	 */
	public final DocumentRange getTitle() {
		return this.title;
	}

	/**
	 * Set the title of this document.
	 *
	 * @param theTitle
	 *            The DocumentRange that specifies the title.
	 */
	public final void setTitle(final DocumentRange theTitle) {
		this.title = theTitle;
		this.title.setSource(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		final StringBuilder result = new StringBuilder();
		for (final DocumentRange span : getContents()) {
			result.append(span.toString());
			result.append("\n");
		}
		return result.toString();
	}
}
