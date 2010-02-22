package org.encog.neural.networks.training.lma;

/**
 * Calculate the Jacobian using the chain rule.
 */
public interface ComputeJacobian {
	
	/**
	 * Calculate the Jacobian.
	 * @param weights The neural network weights and thresholds.
	 * @return The sum of squared errors.
	 */
	double calculate(double[] weights);
	
	/**
	 * @return The Jacobian matrix after it is calculated.
	 */
	double[][] getJacobian();

	/**
	 * @return The errors for each row of the matrix.
	 */
	double[] getRowErrors();

}
