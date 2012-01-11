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
package org.encog.util.normalize.output.mapped;

import java.io.Serializable;


/**
 * Simple class that is used internally to hold a range mapping.
 */
public class MappedRange implements Serializable {

	/**
	 * The low value for the range.
	 */
	private final double low;
	
	/**
	 * The high value for the range.
	 */
	private final double high;
	
	/**
	 * The value that should be returned for this range.
	 */
	private final double value;

	/**
	 * Construct the range mapping.
	 * @param low The low value for the range.
	 * @param high The high value for the range.
	 * @param value The value that this range represents.
	 */
	public MappedRange(final double low, final double high, 
			final double value) {
		super();
		this.low = low;
		this.high = high;
		this.value = value;
	}

	/**
	 * @return The high value for this range.
	 */	
	public double getHigh() {
		return this.high;
	}

	/**
	 * @return The low value for this range.
	 */
	public double getLow() {
		return this.low;
	}

	/**
	 * @return The value that this range represents.
	 */
	public double getValue() {
		return this.value;
	}

	/**
	 * Determine if the specified value is in the range.
	 * @param d The value to check.
	 * @return True if this value is within the range.
	 */
	public boolean inRange(final double d) {
		if ((d >= this.low) && (d <= this.high)) {
			return true;
		}
		return false;
	}

}
