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
	 * The logger.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
	public String getType() {
		return this.type;
	}

	/**
	 * @return True if this is autosend, which means that the type is NOT
	 *         submit. This prevents a form that has multiple submit buttons
	 *         from sending ALL of them in a single post.
	 */
	@Override
	public boolean isAutoSend() {
		return !this.type.equalsIgnoreCase("submit");
	}

	/**
	 * Set the type of this input element.
	 *
	 * @param type
	 *            The type of this input element.
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("[Input:");
		builder.append("type=");
		builder.append(getType());
		builder.append(",name=");
		builder.append(getName());
		builder.append(",value=");
		builder.append(getValue());
		builder.append("]");
		return builder.toString();
	}
}
