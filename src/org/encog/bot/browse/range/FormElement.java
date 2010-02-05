/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.bot.browse.range;

import org.encog.bot.browse.WebPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A document range that represents one individual component to a form.
 * 
 * @author jheaton
 * 
 */
public abstract class FormElement extends DocumentRange {

	/**
	 * The name of this form element.
	 */
	private String name;

	/**
	 * The value held by this form element.
	 */
	private String value;

	/**
	 * The owner of this form element.
	 */
	private Form owner;

	/**
	 * The logger.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct a form element from the specified web page.
	 * 
	 * @param source
	 *            The page that holds this form element.
	 */
	public FormElement(final WebPage source) {
		super(source);
	}

	/**
	 * @return The name of this form.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return The owner of this form element.
	 */
	public Form getOwner() {
		return this.owner;
	}

	/**
	 * @return The value of this form element.
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * @return True if this is autosend, which means that the type is NOT
	 *         submit. This prevents a form that has multiple submit buttons
	 *         from sending ALL of them in a single post.
	 */
	public abstract boolean isAutoSend();

	/**
	 * Set the name of this form element.
	 * 
	 * @param name
	 *            The name of this form element.
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Set the owner of this form element.
	 * 
	 * @param owner
	 *            The owner of this form element.
	 */
	public void setOwner(final Form owner) {
		this.owner = owner;
	}

	/**
	 * Set the value for this form element.
	 * 
	 * @param value
	 *            The value for this form element.
	 */
	public void setValue(final String value) {
		this.value = value;
	}

}
