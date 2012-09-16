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

	public MultiDimension(double[] theArray, int theStart, int theSize) {
		this.dimensions = new int[theSize];
		for(int i=0;i<theSize;i++) {
			this.dimensions[i] = (int)theArray[i];
		}
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
