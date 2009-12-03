package org.encog.util.math.rbf;

/**
 * A multi-dimension RBF.
 */
public interface RadialBasisFunctionMulti {
	/**
	 * Calculate the RBF result for the specified value.
	 * 
	 * @param x
	 *            The value to be passed into the RBF.
	 * @return The RBF value.
	 */
	double calculate(double[] x);

	/**
	 * Get the center of this RBD.
	 * @param The dimension to get the center for.
	 * @return The center of the RBF.
	 */
	double getCenter(int dimension);

	/**
	 * Get the center of this RBD.
	 * @param The dimension to get the center for.
	 * @return The center of the RBF.
	 */
	double getPeak();

	/**
	 * Get the center of this RBD.
	 * @param The dimension to get the center for.
	 * @return The center of the RBF.
	 */
	double getWidth(int dimension);
	
	/**
	 * @return The dimensions in this RBF.
	 */
	int getDimensions();	
}
