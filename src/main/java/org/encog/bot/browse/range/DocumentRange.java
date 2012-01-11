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
package org.encog.bot.browse.range;

import java.util.ArrayList;
import java.util.List;

import org.encog.bot.browse.WebPage;
import org.encog.bot.dataunit.DataUnit;
import org.encog.bot.dataunit.TextDataUnit;

/**
 * Base class that represents a document range. A document range is a collection
 * of tags that all apply to one "concept". For example, a Form, or a Link. This
 * allows the form to collect the elements inside the form, or a link to collect
 * the text along with the link tag.
 *
 * @author jheaton
 *
 */
public class DocumentRange {

	/**
	 * The beginning index for this range.
	 */
	private int begin;

	/**
	 * The ending index for this range.
	 */
	private int end;

	/**
	 * The source page for this range.
	 */
	private WebPage source;

	/**
	 * The id attribute, on the source tag. Useful for DIV tags.
	 */
	private String idAttribute;

	/**
	 * The class attribute. on the source tag.
	 */
	private String classAttribute;

	/**
	 * Sub elements of this range.
	 */
	private final List<DocumentRange> elements = new ArrayList<DocumentRange>();

	/**
	 * The parent to this range, or null if top.
	 */
	private DocumentRange parent;


	/**
	 * Construct a document range from the specified WebPage.
	 *
	 * @param theSource
	 *            The web page that this range belongs to.
	 */
	public DocumentRange(final WebPage theSource) {
		this.source = theSource;
	}

	/**
	 * Add an element.
	 *
	 * @param element
	 *            The element to add.
	 */
	public final void addElement(final DocumentRange element) {
		this.elements.add(element);
		element.setParent(this);
	}

	/**
	 * @return The beginning index.
	 */
	public final int getBegin() {
		return this.begin;
	}

	/**
	 * @return the classAttribute
	 */
	public final String getClassAttribute() {
		return this.classAttribute;
	}

	/**
	 * @return The elements of this document range.
	 */
	public final List<DocumentRange> getElements() {
		return this.elements;
	}

	/**
	 * @return The ending index.
	 */
	public final int getEnd() {
		return this.end;
	}

	/**
	 * @return the idAttribute
	 */
	public final String getIdAttribute() {
		return this.idAttribute;
	}

	/**
	 * @return The web page that owns this class.
	 */
	public final DocumentRange getParent() {
		return this.parent;
	}

	/**
	 * @return The web page that this range is owned by.
	 */
	public final WebPage getSource() {
		return this.source;
	}

	/**
	 * Get the text from this range.
	 *
	 * @return The text from this range.
	 */
	public final String getTextOnly() {
		final StringBuilder result = new StringBuilder();

		for (int i = getBegin(); i < getEnd(); i++) {
			final DataUnit du = this.source.getData().get(i);
			if (du instanceof TextDataUnit) {
				result.append(du.toString());
				result.append("\n");
			}
		}

		return result.toString();
	}

	/**
	 * Set the beginning index.
	 *
	 * @param theBegin
	 *            The beginning index.
	 */
	public final void setBegin(final int theBegin) {
		this.begin = theBegin;
	}

	/**
	 * @param theClassAttribute
	 *            the classAttribute to set
	 */
	public final void setClassAttribute(final String theClassAttribute) {
		this.classAttribute = theClassAttribute;
	}

	/**
	 * Set the ending index.
	 *
	 * @param theEnd
	 *            The ending index.
	 */
	public final void setEnd(final int theEnd) {
		this.end = theEnd;
	}

	/**
	 * @param id
	 *            the idAttribute to set
	 */
	public final void setIdAttribute(final String id) {
		this.idAttribute = id;
	}

	/**
	 * Set the parent.
	 *
	 * @param theParent
	 *            The parent.
	 */
	public final void setParent(final DocumentRange theParent) {
		this.parent = theParent;
	}

	/**
	 * Set the source web page.
	 *
	 * @param theSource
	 *            The source web page.
	 */
	public final void setSource(final WebPage theSource) {
		this.source = theSource;
	}


}
