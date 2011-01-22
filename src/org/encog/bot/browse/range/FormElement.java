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
