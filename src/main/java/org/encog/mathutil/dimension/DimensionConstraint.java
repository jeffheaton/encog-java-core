/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.mathutil.dimension;

/**
 * Specifies a constraint for dimensions, using a lower and upper bound.
 * 
 * @author jheaton
 * 
 */
public class DimensionConstraint {

	/**
	 * The lower bound for each dimension.
	 */
	private final MultiDimension lower;

	/**
	 * The upper bound for each dimension.
	 */
	private final MultiDimension upper;

	/**
	 * Construct the constraint.
	 * 
	 * @param n
	 *            The number of dimensions.
	 * @param theLower
	 *            The lower bound for each dimension.
	 * @param theUpper
	 *            The upper bound for each dimension.
	 */
	public DimensionConstraint(int n, int theLower, int theUpper) {
		this.lower = new MultiDimension(n);
		this.upper = new MultiDimension(n);

		for (int i = 0; i < n; i++) {
			this.lower.setDimension(i, theLower);
			this.upper.setDimension(i, theUpper);
		}
	}

	/**
	 * @return The lower bound for each dimension.
	 */
	public MultiDimension getLower() {
		return lower;
	}

	/**
	 * @return The upper bound for each dimension.
	 */
	public MultiDimension getUpper() {
		return upper;
	}

	/**
	 * Get the lower bound for a specific dimension.
	 * 
	 * @param d
	 *            The dimension.
	 * @return The lower bound for the specified dimension.
	 */
	public int getLower(int d) {
		return lower.getDimension(d);
	}

	/**
	 * Get the upper bound for a specific dimension.
	 * 
	 * @param d
	 *            The dimension.
	 * @return The upper bound for the specified dimension.
	 */
	public int getUpper(int d) {
		return upper.getDimension(d);
	}

	/**
	 * Get the range (between upper and lower bound) for the specified
	 * dimension.
	 * 
	 * @param d
	 *            The dimension.
	 * @return The range for the specified dimension.
	 */
	public int getRange(int d) {
		return (upper.getDimension(d) - lower.getDimension(d))+1;
	}

	public int getMiddle(int d) {
		return getRange(d)/2;
	}

}
