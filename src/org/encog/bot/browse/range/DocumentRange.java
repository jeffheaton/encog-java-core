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
package org.encog.bot.browse.range;

import java.util.ArrayList;
import java.util.List;

import org.encog.bot.browse.WebPage;
import org.encog.bot.dataunit.DataUnit;
import org.encog.bot.dataunit.TextDataUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * The logger.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct a document range from the specified WebPage.
	 * 
	 * @param source
	 *            The web page that this range belongs to.
	 */
	public DocumentRange(final WebPage source) {
		this.source = source;
	}

	/**
	 * Add an element.
	 * 
	 * @param element
	 *            The element to add.
	 */
	public void addElement(final DocumentRange element) {
		this.elements.add(element);
		element.setParent(this);
	}

	/**
	 * @return The beginning index.
	 */
	public int getBegin() {
		return this.begin;
	}

	/**
	 * @return the classAttribute
	 */
	public String getClassAttribute() {
		return this.classAttribute;
	}

	/**
	 * @return The elements of this document range.
	 */
	public List<DocumentRange> getElements() {
		return this.elements;
	}

	/**
	 * @return The ending index.
	 */
	public int getEnd() {
		return this.end;
	}

	/**
	 * @return the idAttribute
	 */
	public String getIdAttribute() {
		return this.idAttribute;
	}

	/**
	 * @return The web page that owns this class.
	 */
	public DocumentRange getParent() {
		return this.parent;
	}

	/**
	 * @return The web page that this range is owned by.
	 */
	public WebPage getSource() {
		return this.source;
	}

	/**
	 * Get the text from this range.
	 * 
	 * @return The text from this range.
	 */
	public String getTextOnly() {
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
	 * @param begin
	 *            The beginning index.
	 */
	public void setBegin(final int begin) {
		this.begin = begin;
	}

	/**
	 * @param classAttribute
	 *            the classAttribute to set
	 */
	public void setClassAttribute(final String classAttribute) {
		this.classAttribute = classAttribute;
	}

	/**
	 * Set the ending index.
	 * 
	 * @param end
	 *            The ending index.
	 */
	public void setEnd(final int end) {
		this.end = end;
	}

	/**
	 * @param idAttribute
	 *            the idAttribute to set
	 */
	public void setIdAttribute(final String idAttribute) {
		this.idAttribute = idAttribute;
	}

	/**
	 * Set the parent.
	 * 
	 * @param parent
	 *            The parent.
	 */
	public void setParent(final DocumentRange parent) {
		this.parent = parent;
	}

	/**
	 * Set the source web page.
	 * 
	 * @param source
	 *            The source web page.
	 */
	public void setSource(final WebPage source) {
		this.source = source;
	}

	/**
	 * @return This object as a string.
	 */
	@Override
	public String toString() {
		return getTextOnly();
	}

}
