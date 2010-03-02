/*
 * Encog(tm) Core v2.4
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
	 * @return This object as a string.
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
