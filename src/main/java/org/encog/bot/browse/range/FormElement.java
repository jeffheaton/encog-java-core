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

import org.encog.bot.browse.WebPage;

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
	public final String getName() {
		return this.name;
	}

	/**
	 * @return The owner of this form element.
	 */
	public final Form getOwner() {
		return this.owner;
	}

	/**
	 * @return The value of this form element.
	 */
	public final String getValue() {
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
	 * @param theName
	 *            The name of this form element.
	 */
	public final void setName(final String theName) {
		this.name = theName;
	}

	/**
	 * Set the owner of this form element.
	 * 
	 * @param theOwner
	 *            The owner of this form element.
	 */
	public final void setOwner(final Form theOwner) {
		this.owner = theOwner;
	}

	/**
	 * Set the value for this form element.
	 * 
	 * @param theValue
	 *            The value for this form element.
	 */
	public final void setValue(final String theValue) {
		this.value = theValue;
	}

	/** {@inheritDoc} */
	public final String toString() {
		StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" name=");
		result.append(this.name);
		result.append(", value=");
		result.append(this.value);
		result.append("]");
		return result.toString();
	}
}
