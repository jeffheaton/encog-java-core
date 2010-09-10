/*
 * Encog(tm) Core v2.5
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

import org.encog.bot.browse.Address;
import org.encog.bot.browse.WebPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * The logger.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
	public Input findType(final String type, final int index) {
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
	public Address getAction() {
		return this.action;
	}

	/**
	 * @return How the form will be sent.
	 */
	public Method getMethod() {
		return this.method;
	}

	/**
	 * Set the action for the form.
	 *
	 * @param action
	 *            The URL to send the form to.
	 */
	public void setAction(final Address action) {
		this.action = action;
	}

	/**
	 * Set the method to send the form.
	 *
	 * @param method
	 *            How to send the form.
	 */
	public void setMethod(final Method method) {
		this.method = method;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
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
