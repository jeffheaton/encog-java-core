/*
 * Encog(tm) Core v2.6 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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
package org.encog.engine.util;

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
	 * @param high
	 *            The high end of the range.
	 * @param low
	 *            The low end of the range.
	 */
	public IntRange(final int high, final int low) {
		super();
		this.high = high;
		this.low = low;
	}

	/**
	 * @return The high end of the range.
	 */
	public int getHigh() {
		return this.high;
	}

	/**
	 * @return The low end of the range.
	 */
	public int getLow() {
		return this.low;
	}

	/**
	 * Set the high end of the range.
	 * 
	 * @param high
	 *            The high end of the range.
	 */
	public void setHigh(final int high) {
		this.high = high;
	}

	/**
	 * Set the low end of the range.
	 * 
	 * @param low
	 *            The low end of the range.
	 */
	public void setLow(final int low) {
		this.low = low;
	}

}
