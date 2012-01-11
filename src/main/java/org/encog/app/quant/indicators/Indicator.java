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
package org.encog.app.quant.indicators;

import java.util.Map;

import org.encog.app.analyst.csv.basic.BaseCachedColumn;
import org.encog.app.quant.QuantError;

/**
 * An indicator, used by Encog.
 */
public abstract class Indicator extends BaseCachedColumn {

	/**
	 * The beginning index.
	 */
	private int beginningIndex;

	/**
	 * The ending index.
	 */
	private int endingIndex;

	/**
	 * Construct the indicator.
	 * 
	 * @param name
	 *            The indicator name.
	 * @param input
	 *            Is this indicator used to predict?
	 * @param output
	 *            Is this indicator what we are trying to predict.
	 */
	public Indicator(final String name, final boolean input,
			final boolean output) {
		super(name, input, output);
	}

	/**
	 * Calculate this indicator.
	 * 
	 * @param data
	 *            The data available to this indicator.
	 * @param length
	 *            The length of data to use.
	 */
	public abstract void calculate(Map<String, BaseCachedColumn> data,
			int length);

	/**
	 * @return the beginningIndex
	 */
	public final int getBeginningIndex() {
		return this.beginningIndex;
	}

	/**
	 * @return the endingIndex
	 */
	public final int getEndingIndex() {
		return this.endingIndex;
	}

	/**
	 * @return The number of periods this indicator is for.
	 */
	public abstract int getPeriods();

	/**
	 * Require a specific type of underlying data.
	 * 
	 * @param theData
	 *            The data available.
	 * @param item
	 *            The type of data we are looking for.
	 */
	public final void require(final Map<String, BaseCachedColumn> 
		theData, final String item) {
		if (!theData.containsKey(item)) {
			throw new QuantError(
					"To use this indicator, the underlying data must contain: "
							+ item);
		}
	}

	/**
	 * @param theBeginningIndex
	 *            the beginningIndex to set
	 */
	public final void setBeginningIndex(final int theBeginningIndex) {
		this.beginningIndex = theBeginningIndex;
	}

	/**
	 * @param theEndingIndex
	 *            the endingIndex to set.
	 */
	public final void setEndingIndex(final int theEndingIndex) {
		this.endingIndex = theEndingIndex;
	}

}
