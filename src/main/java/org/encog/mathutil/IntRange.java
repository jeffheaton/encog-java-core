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
package org.encog.mathutil;

/**
 * A range of integers.
 */
public class IntRange {

	/**
	 * The low end of the range.
	 */
	private int high;

	/**
	 * The high end of the range.
	 */
	private int low;

	/**
	 * Construct an integer range.
	 * 
	 * @param theHigh
	 *            The high end of the range.
	 * @param theLow
	 *            The low end of the range.
	 */
	public IntRange(final int theHigh, final int theLow) {
		super();
		this.high = theHigh;
		this.low = theLow;
	}

	/**
	 * @return The high end of the range.
	 */
	public final int getHigh() {
		return this.high;
	}

	/**
	 * @return The low end of the range.
	 */
	public final int getLow() {
		return this.low;
	}

	/**
	 * Set the high end of the range.
	 * 
	 * @param theHigh
	 *            The high end of the range.
	 */
	public final void setHigh(final int theHigh) {
		this.high = theHigh;
	}

	/**
	 * Set the low end of the range.
	 * 
	 * @param theLow
	 *            The low end of the range.
	 */
	public final void setLow(final int theLow) {
		this.low = theLow;
	}

}
