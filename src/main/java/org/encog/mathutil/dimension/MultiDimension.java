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

import java.util.Arrays;

/**
 * Handle multi-dimensional integer-based dimensions. Depending on how the
 * values are interpreted, this structure can be used to either represent a
 * coordinate, velocity or a constraint.
 * 
 * @author jheaton
 * 
 */
public class MultiDimension {

	/**
	 * The dimensions.
	 */
	private final int[] dimensions;
	

	/**
	 * Allocate a MultiDimension.
	 * 
	 * @param n
	 *            The number of dimensions.
	 */
	public MultiDimension(int n) {
		this.dimensions = new int[n];
	}

	public MultiDimension(MultiDimension lower) {
		this.dimensions = lower.getDimensions().clone();
	}

	/**
	 * Get a dimension.
	 * 
	 * @param d
	 *            The dimension to get.
	 * @return The value of the specified dimension.
	 */
	public int getDimension(int d) {
		return this.dimensions[d];
	}

	/**
	 * @return The number of dimensions.
	 */
	public int size() {
		return this.dimensions.length;
	}

	/**
	 * @return The dimensions as an array.
	 */
	public int[] getDimensions() {
		return dimensions;
	}

	/**
	 * Roll the dimension forward by one. Start with the low dimension and tick
	 * forward. This can be used to iterate through every index position.
	 * 
	 * @param constraint
	 *            The dimension constraints.
	 * @return True if there are still more combinations, false if we are done.
	 */
	public boolean forward(DimensionConstraint constraint) {
		int i = 0;
		do {
			this.dimensions[i]++;

			// is this hidden layer still within the range?
			if (this.dimensions[i] <= constraint.getUpper(i)) {
				return true;
			}

			// increase the next layer if we've maxed out this one
			this.dimensions[i] = constraint.getLower(i);
			i++;

		} while (i < size());

		// can't increase anymore, we're done!

		return false;
	}

	/**
	 * Flatten the multi-dimensional index into a single dimension index.
	 * 
	 * @param constraint
	 *            The dimension constraints.
	 * @return The flat 1d index.
	 */
	public int flatten(DimensionConstraint constraint) {
		int result = 0;
		int mult = 1;

		for (int i = 0; i < size(); i++) {
			result += (mult * this.dimensions[i]);
			mult *= constraint.getRange(i);
		}
		return result;
	}

	/**
	 * Set a single dimension.
	 * 
	 * @param d
	 *            The dimension to set.
	 * @param value
	 *            The new value.
	 */
	public void setDimension(int d, int value) {
		this.dimensions[d] = value;

	}
	
	public String toString() {
		return Arrays.toString(this.dimensions);
	}

	public double calculateLowerStep(DimensionConstraint constraint, int d) {
		if( this.dimensions[d]<=constraint.getLower(d))
			return 0;
		else
			return -1;
	}

	public double calculateUpperStep(DimensionConstraint constraint, int d) {
		if( this.dimensions[d]<=constraint.getLower(d))
			return 0;
		else
			return 1;
	}

}
