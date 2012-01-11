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
package org.encog.util.normalize.segregate;

import java.io.Serializable;


/**
 * Specifies a range that might be included or excluded.
 */
public class SegregationRange implements Serializable {

	/**
	 * The low end of this range.
	 */
	private double low;

	/**
	 * The high end of this range.
	 */
	private double high;

	/**
	 * Should this range be included.
	 */
	private boolean include;

	/**
	 * Default constructor for reflection.
	 */
	public SegregationRange() {

	}

	/**
	 * Construct a segregation range.
	 * 
	 * @param low
	 *            The low end of the range.
	 * @param high
	 *            The high end of the range.
	 * @param include
	 *            Specifies if the range should be included.
	 */
	public SegregationRange(final double low, final double high,
			final boolean include) {
		super();
		this.low = low;
		this.high = high;
		this.include = include;
	}

	/**
	 * @return The high end of the range.
	 */
	public double getHigh() {
		return this.high;
	}

	/**
	 * @return The low end of the range.
	 */
	public double getLow() {
		return this.low;
	}

	/**
	 * Is this value within the range.
	 * @param value The value to check.
	 * @return True if the value is within the range.
	 */
	public boolean inRange(final double value) {
		return ((value >= this.low) && (value <= this.high));
	}

	/**
	 * @return True if this range should be included.
	 */
	public boolean isIncluded() {
		return this.include;
	}

}
