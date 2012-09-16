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
	 * @param theUpper
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

}
