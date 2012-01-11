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
 * A document range that specifies a span tag, and any embedded tags.
 *
 * @author jheaton
 *
 */
public class Span extends DocumentRange {

	/**
	 * Construct a span range from the specified web page.
	 *
	 * @param source
	 *            The source web page.
	 */
	public Span(final WebPage source) {
		super(source);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[Span:class=");
		result.append(getClassAttribute());
		result.append(",id=");
		result.append(getIdAttribute());
		result.append(",elements=");
		result.append(getElements().size());
		result.append("]");
		return result.toString();
	}
}
