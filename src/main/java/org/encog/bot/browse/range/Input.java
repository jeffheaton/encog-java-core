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
 * A form element that represents for input for text. These are of the form
 * name=value.
 *
 * @author jheaton
 *
 */
public class Input extends FormElement {

	/**
	 * The type of input element that this is.
	 */
	private String type;

	/**
	 * Construct this Input element.
	 *
	 * @param source
	 *            The source for this input element.
	 */
	public Input(final WebPage source) {
		super(source);
	}

	/**
	 * @return The type of this input.
	 */
	public final String getType() {
		return this.type;
	}

	/**
	 * @return True if this is autosend, which means that the type is NOT
	 *         submit. This prevents a form that has multiple submit buttons
	 *         from sending ALL of them in a single post.
	 */
	@Override
	public final boolean isAutoSend() {
		return !this.type.equalsIgnoreCase("submit");
	}

	/**
	 * Set the type of this input element.
	 *
	 * @param theType
	 *            The type of this input element.
	 */
	public final void setType(final String theType) {
		this.type = theType;
	}

}
