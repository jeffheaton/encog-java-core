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
 * A document range that represents a hyperlink, and any embedded tags and text.
 *
 * @author jheaton
 *
 */
public class Link extends DocumentRange {

	/**
	 * The target address for this link.
	 */
	private Address target;

	/**
	 * The logger.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct a link from the specified web page.
	 *
	 * @param source
	 *            The web page this link is from.
	 */
	public Link(final WebPage source) {
		super(source);
	}

	/**
	 * @return The target of this link.
	 */
	public Address getTarget() {
		return this.target;
	}

	/**
	 * Set the target of this link.
	 *
	 * @param target
	 *            The link target.
	 */
	public void setTarget(final Address target) {
		this.target = target;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[Link:");
		result.append(this.target);
		result.append("|");
		result.append(getTextOnly());
		result.append("]");
		return result.toString();
	}

}
