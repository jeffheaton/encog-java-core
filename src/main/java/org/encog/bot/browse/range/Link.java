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
	public final Address getTarget() {
		return this.target;
	}

	/**
	 * Set the target of this link.
	 *
	 * @param theTarget
	 *            The link target.
	 */
	public final void setTarget(final Address theTarget) {
		this.target = theTarget;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[Link:");
		result.append(this.target);
		result.append("|");
		result.append(getTextOnly());
		result.append("]");
		return result.toString();
	}

}
