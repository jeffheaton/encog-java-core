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

import org.encog.bot.browse.Address;
import org.encog.bot.browse.WebPage;

/**
 * A document range that represents a form, and all embedded tags.
 *
 * @author jheaton
 *
 */
public class Form extends DocumentRange {

	/**
	 * The method for this form.
	 *
	 * @author jheaton
	 *
	 */
	public enum Method {
		/**
		 * This form is to be POSTed.
		 */
		POST,
		/**
		 * THis form is to sent using a GET.
		 */
		GET
	};

	/**
	 * The address that the form will be sent to.
	 */
	private Address action;

	/**
	 * The means by which the form will be sent.
	 */
	private Method method;

	/**
	 * Construct a form on the specified web page.
	 *
	 * @param source
	 *            The web page that contains this form.
	 */
	public Form(final WebPage source) {
		super(source);
	}

	/**
	 * Find the form input by type.
	 *
	 * @param type
	 *            The type of input we want.
	 * @param index
	 *            The index to begin searching at.
	 * @return The Input object that was found.
	 */
	public final Input findType(final String type, final int index) {
		int i = index;

		for (final DocumentRange element : getElements()) {
			if (element instanceof Input) {
				final Input input = (Input) element;
				if (input.getType().equalsIgnoreCase(type)) {
					if (i <= 0) {
						return input;
					}
					i--;
				}
			}
		}
		return null;
	}

	/**
	 * @return The URL to send the form to.
	 */
	public final Address getAction() {
		return this.action;
	}

	/**
	 * @return How the form will be sent.
	 */
	public final Method getMethod() {
		return this.method;
	}

	/**
	 * Set the action for the form.
	 *
	 * @param theAction
	 *            The URL to send the form to.
	 */
	public final void setAction(final Address theAction) {
		this.action = theAction;
	}

	/**
	 * Set the method to send the form.
	 *
	 * @param theMethod
	 *            How to send the form.
	 */
	public final void setMethod(final Method theMethod) {
		this.method = theMethod;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("[Form:");
		builder.append("method=");
		builder.append(getMethod());
		builder.append(",action=");
		builder.append(getAction());
		for (final DocumentRange element : getElements()) {
			builder.append("\n\t");
			builder.append(element.toString());
		}
		builder.append("]");
		return builder.toString();
	}

}
